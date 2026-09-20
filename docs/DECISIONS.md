# Decisões Arquiteturais

## 2026-09-20 — Router v5: spawn, storage de rotas e lifecycle movidos para megalodonte-base

**Problema**: `megalodonte-router` v5 concentrava três responsabilidades no mesmo `Router` —
guardar as rotas (record aninhado `Router.Route` + matching `${param}` em `resolveRoute`),
gerenciar janelas spawnadas (`spawnWindow`, `spawnedWindowList`, `destroyAndCloseStage`) e
navegar. Isso duplicava tipos que já existiam (ou estavam sendo movidos) para a base (`Route`,
`ScreenFactory`, `RouteResolutionException`, `RouteNotFoundException`) e mantinha estado de janela
(`spawnedWindowList`) num local marcado com `TODO: talvez devesse ficar no contexto da aplicação
base`. O `spawnWindow` inteiro morava no Router — para o Router "só navegar", ele precisava sair.

**Decisão**:
1. `megalodonte-base` vira dono das rotas e do lifecycle de telas:
   - `megalodonte.base.route.RouteTable`: guarda o `Set<Route>` + entrypoint e resolve o caminho
     (matching `${param}`) — o "guardar as Route" passa a ser responsabilidade da base.
   - `megalodonte.base.route.ScreenManager`: concentra `Map<Stage, ActiveScreen>`,
     `List<Stage> spawnedWindows`, `mount()` (destrói a tela anterior, cria o `ScreenContextBase`,
     cria a screen, chama `onMount`), `destroy()`, `closeAllSpawned()` e `extractView()` — o ciclo
     de vida (`scope().cancel()` + `onDestroy()` nos pontos de teardown) sai do Router e vira
     única fonte da verdade, compartilhada por navegação e spawn.
   - `ScreenContextBase` (v2) implementa `spawnWindow(path)` / `spawnWindow(path, errorHandler)`
     de verdade — cria a `Stage`, resolve+monta via `ScreenManager`, aplica `RouteProps` (título,
     ícone, resizable), mostra e registra o `setOnCloseRequest` (destroy via `ScreenManager`).
     `ScreenContextInterface` (v2) ganha as duas assinaturas de spawn.
2. `RouterBase` volta a ser navegação pura: `bind`, `entrypoint`, `navigateOnStage`,
   `navigateAndCloseOthers`, `mainStage`. Removido `spawnWindow` do contrato.
3. Router v5 (`megalodonte.router.v5.Router`) passa a ser apenas navegação: resolve via
   `RouteTable` e monta via `ScreenManager`. Removidos do pacote v5: `ScreenContext` (subclasse
   vazia), `ScreenFactory` (dup) e `RouteResolutionException` (dup) — os tipos equivalentes agora
   vivem na base.
4. v4 (`megalodonte.router.v4`) e os apps consumidores (`balanca-gobitech`, `plics-sw`) NÃO são
   tocados — continuam na própria API (`ctx.router().spawnWindow(...)`). Sem impacto de regressão.

**Uso novo (v5)**:
```java
RouteTable table = new RouteTable(new AppRoutes().routes(), Screens.SPLASH.name());
context.useRouter(new Router(table)).start();
// dentro das telas:
ctx.spawnWindow(path); // antes: ctx.router().spawnWindow(path)
```

**Validação**: `megalodonte-base` publicado em `mavenLocal` (`publishToMavenLocal`) e
`megalodonte-router` compilado contra a base nova.

## 2026-08-19 — Revertido `setMaxWidth`/`setMaxHeight` em `ScreenContext.applyStageProps`

Adicionado ontem (2026-08-18) pra impedir uma Stage `resizable=true` de crescer sozinha além do
tamanho declarado da rota — travava também o usuário maximizando de propósito, contradizendo
`screenIsExpandable=true`. Revertido; ver `balanca-gobitech/docs/DECISIONS.md` (mesma data) pro
relato completo, incluindo por que o problema original já tinha correção no nível certo (no
conteúdo, não na janela) antes desse teto ter sido adicionado.

## 2026-08-18 — `ListState.set()` bloqueava notificação quando `updateIf()` mutava e devolvia a mesma referência

**Problema**: usuário relatou no `balanca-gobitech` que editar um Cliente e salvar não atualizava
a tabela ao voltar pra lista. Achado por leitura de código, confirmado com um teste isolado antes
de mexer: `ListState.set(List<E> newList)` tinha uma guarda `Objects.equals(this.value, newList)`
pra evitar notificar listeners à toa — mas `Objects.equals` numa `List` compara **conteúdo**
elemento a elemento via `equals()` de cada item, não identidade da lista em si.

`updateIf()` (chamado por todo `handleAddOrUpdate()` de tela CRUD ao editar) muta o objeto **já
presente** na lista e devolve a **mesma referência** pro updater — padrão comum (`model =
clienteSelecionado.get()`, muta os setters, devolve `model`). A "lista nova" construída por
`updateIf()` tem exatamente os mesmos objetos (mesmas referências) da lista antiga — como a
maioria dos Models do app só tem `@Getter @Setter` (sem `@EqualsAndHashCode`), a comparação usa
`Object.equals()` (identidade) por elemento, então `newList.equals(oldList)` dava `true` mesmo
com os campos do objeto tendo mudado de verdade — `set()` cancelava a notificação achando que
"nada mudou". A tabela só voltava a mostrar o dado certo depois de um refetch completo do banco
(trocar de seção e voltar), nunca imediatamente ao salvar.

**Decisão**: trocar a guarda de `Objects.equals(this.value, newList)` (conteúdo) pra
`this.value == newList` (identidade da lista). Continua bloqueando o caso realmente redundante —
chamar `set()` de novo com a exata mesma referência de lista — sem falso-negativo quando o
conteúdo mudou mas os objetos internos são os mesmos.

**Testado**: `ListStateUpdateIfBugTest` (`megalodonte-reactivity`) — reproduz o cenário exato
(item mutado in-place, `updateIf` devolvendo a mesma referência) e falhava antes do fix. Depois
do fix, passa. `megalodonte-reactivity` republicado em `mavenLocal`; validado ponta a ponta contra
`balanca-gobitech` (`--refresh-dependencies compileJava test`: 155/155, sem regressão).

## 2026-08-18 — `Scope` em `megalodonte.base.async`: cancelamento de trabalho assíncrono vinculado ao ciclo de vida

**Problema**: `Async.Run()` é fire-and-forget puro — não retorna handle, não tem vínculo com nenhuma tela/ViewModel. Isso deixa uma corrida estrutural em qualquer tela que abra algo assíncrono no `onMount()` (conexão, listener, timer) e feche em `onDestroy()`: se `onDestroy()` rodar antes da tarefa assíncrona terminar de adquirir o recurso, o `null`-check de guarda não pega nada, o recurso termina de abrir depois e nunca mais é fechado — a tarefa segura referência viva pro objeto todo via closure, impedindo o GC. Achado na prática no app `balanca-gobitech` (`PesagemViewModel.iniciarLeituraBalanca()`/`pararLeituraBalanca()`, ver `DECISIONS.md` de lá) e corrigido ali com uma flag `destruido` manual — mas o gap é de framework, não desse app: qualquer novo dev pode reintroduzir a mesma corrida.

**Decisão — Fase 1 (esta entrada), isolada e aditiva**: `megalodonte.base.async.Scope` — cobre um ciclo de vida (uma tela, uma ViewModel), não reutilizável depois de cancelado:
- `run(RunnableThrowing)`: roda a task numa thread virtual só se o escopo ainda não tiver sido cancelado no instante em que ela começa a executar.
- `onCancel(Runnable)`: registra limpeza pra rodar quando `cancel()` acontecer — ou dispara na hora, síncrono, se o escopo já estava cancelado no momento do registro (cobre o caso do recurso que terminou de abrir depois do cancelamento).
- `cancel()`/`isCancelled()`: idempotente, síncrono, barato — mesma filosofia do `job.cancel()` do Kotlin (cancelar é instantâneo, quem termina de forma assíncrona é o trabalho em si, cooperando).
- Testado em `ScopeTest` (7 casos, JUnit puro, sem JavaFX) — `./gradlew test` em `megalodonte-base`: BUILD SUCCESSFUL.

**Decisão — Fase 2 (2026-08-18, mesma sessão): cancelamento automático no Router**. `megalodonte-base` publicado em `mavenLocal` (`./gradlew publishToMavenLocal`). Em seguida, no `megalodonte-router` (v4):
- `ScreenContext` ganhou um `Scope` próprio, criado junto no construtor, exposto via `ctx.scope()`.
- `Router.activeScreens` passou de `Map<Stage, ScreenComponent>` pra `Map<Stage, ActiveScreen>` (`ActiveScreen` = record local `(ScreenComponent screen, ScreenContext ctx)`), única mudança de forma — os 3 pontos que já chamavam `.onDestroy()` (`resolveWithStage`, `destroyAndCloseStage`, o `setOnCloseRequest` de `spawnWindow`) agora chamam `ctx.scope().cancel()` **antes** de `screen.onDestroy()`.
- Resultado: qualquer tela que trocar `Async.Run(...)` por `ctx.scope().run(...)` ganha cancelamento automático ao navegar pra fora — sem precisar lembrar de nada, igual `viewModelScope` cancela sozinho no `onCleared()`. API pública do Router não mudou (só um método novo em `ScreenContext`), mudança é retrocompatível — telas que não usam `ctx.scope()` continuam exatamente como antes.
- `megalodonte-router` também publicado em `mavenLocal`. Validado ponta a ponta contra o `balanca-gobitech`: `./gradlew --refresh-dependencies compileJava test` → **BUILD SUCCESSFUL, 155/155 testes**, sem nenhuma mudança de código nesse app (prova que a mudança é aditiva/não-quebra).

**Ainda não feito**: Fase 3 (migrar `PesagemViewModel` do `balanca-gobitech` pra usar `ctx.scope()` em vez da flag `destruido` manual, dogfooding a API onde o bug apareceu) e Fase 4 (auditar `plics-sw` e outros apps Megalodonte atrás do mesmo padrão de risco).

**Motivo**: fechar a mesma classe de bug na raiz (framework) em vez de patch por tela — sem isso, cada app que usa `Async.Run` dentro de `onMount` pra abrir algo de vida longa é candidato a reintroduzir o mesmo vazamento.

## 2026-06-04 — ForEachState movido para megalodonte-base; components sem dependência de reactivity

**Problema**: megalodonte-components importava diretamente megalodonte-reactivity (ForEachState), criando acoplamento desnecessário. Components deveria depender apenas de interfaces de reatividade do pacote base.

**Decisão**:
1. Criada interface `ForEachState<T, C>` em `megalodonte.base.state` (base) — expõe `getComponents()` e `getState()`
2. Implementação concreta mantida em `megalodonte.ForEachState` (reactivity) — implementa a interface da base
3. Removida dependência `megalodonte-reactivity` de megalodonte-components/build.gradle.kts
4. Components usam apenas a interface (`megalodonte.base.state.ForEachState`) — acoplamento frouxo

**Motivo**: megalodonte-base deve conter apenas contratos (interfaces). A implementação (lógica de reconciliação) pertence ao módulo de reatividade. Components depende apenas da abstração, permitindo trocar a implementação sem afetar os componentes.

## 2026-06-03 — Alinhamento à ThemeInterface simplificada

**Problema**: ThemeInterface foi simplificada em megalodonte-base (removido `radius()` e `ThemeRadius`, mesclado em `ThemeBorder`), mas megalodonte-theme e megalodonte-components ainda usavam a API antiga.

**Decisão**:
1. Theme.java: removida declaração redundante de métodos e `radius()` — herda tudo de ThemeInterface
2. DefaultTheme.java: colors() usa ThemeColors base (7 args); border() usa 4 args; removido radius()
3. ThemeManager.java: removidos métodos convenience button*() (cores movidas para ButtonProps)
4. Componentes: theme.radius().md() → theme.border().radiusMd()
5. Cores de botão: movidas de ThemeColors para constantes locais em ButtonProps

**Motivo**: Alinhar os módulos downstream à interface simplificada da base, eliminando código morto e erros de compilação.

## 2026-06-03 — Remoção do marker Theme.java e uso direto de ThemeInterface

**Problema**: `Theme.java` era uma interface vazia (`interface Theme extends ThemeInterface {}`) que não agregava valor, mas criava acoplamento — `ThemeManager` só aceitava `Theme`, impedindo que implementações diretas de `ThemeInterface` fossem registradas.

**Decisão**:
1. Removido `Theme.java` (interface marker vazia)
2. Removido `ThemeColors.java` (14 campos, morto) e seu teste
3. `ThemeManager` agora trabalha com `ThemeInterface` — qualquer implementação pode ser registrada
4. Componentes agora usam `ThemeInterface` em vez de `Theme` em todas as assinaturas
5. `PropsInterface.java` simplificado: removido `applyTheme` do contrato (nunca era usado de fato — o método real é o abstrato em `Props`)
6. `Props.java` limpo: removido dual `applyTheme` e código comentado

**Flexibilidade**: qualquer biblioteca de tema externa faz:
```java
public class MyTheme implements ThemeInterface { ... }
ThemeManager.setTheme(new MyTheme());
```
Sem depender de interface fantasma.

## 2026-06-03 — Context.useRouter builder pattern + RouterBase.entrypoint()

**Problema**: O padrão de uso exigia duas chamadas — `context.useRouter(router)` e `context.useView(router.entrypoint())` — e o retorno de `useRouter` (`RouterBase`) não era usado.

**Mudanças**:
1. `RouterBase` ganhou o método `RouteResult entrypoint()` (v3 e v4 Router já tinham)
2. `Context.useRouter(RouterBase)` faz `bind()` e retorna `Context.RouterBuilder` (tipo intermediário)
3. `RouterBuilder.start()` é o método terminal que chama `useView(router.entrypoint())`
4. v3/Router: removido `RouteResult` e `RouteProps` duplicados — agora usa os da base
5. README atualizado com o novo padrão

**Resultado**:
```java
// Antes
context.useRouter(router);
context.useView(router.entrypoint().view());

// Depois
context.useRouter(router).start();
```

## 2026-07-02 — SelectProps com estilização inline + tema (padrão InputProps)

**Problema**: SelectProps não suportava estilização inline nem fallback ao tema, ao contrário de InputProps, ButtonProps e demais componentes.

**Decisão**:
1. SelectProps muda de `extends Props` para `extends TextComponentProps<SelectProps>` — herda fontSize, fontWeight, textColor
2. Adicionados campos: `bgColor`, `borderColor`, `borderWidth`, `borderRadius` com fluent setters
3. Adicionados `tone(TextTone)` e `variant(TextVariant)` para cor de texto via tema
4. Adicionado `disable()` para desabilitar o ComboBox
5. `applyTheme` usa `StyleUtils.getFinal*()` para fallback ao tema quando valor inline não é definido

**Motivo**: Consistência entre os Props — todos os componentes devem seguir o mesmo padrão de estilização inline + tema.

# Contexto do Projeto

## 2026-10-06 — Contrato multiplataforma inicial

- megalodonte-core agora é dono de estado, tema/tokens/registro, escala e contratos
  neutros. JDK 25 com release 17; core não depende de JavaFX/desktop.
- base JavaFX depende/transmite core e instala JavaFxPlatform no bootstrap.
- megalodonte-theme depende somente de core; o mesmo jar é consumível no Android.
- megalodonte-reactivity-portable usa os mesmos fontes neutros de reactivity;
  Show continua sendo um adapter de plataforma. Não combinar os dois jars reactivity.
- observe/Subscription/unsubscribe, map lazy, close de ComputedState/ForEachState
  e cleanup do ListenerManager permitem teardown real.
- ScreenLifecycle e BackendContract v1 comuns; RouteTable desktop usa RouteMatcher.
- Core/tema/portable testados; todos os módulos desktop recompilados. Android
  compila/testa clientes originais 1–4 e gera APK; API restante ainda é parcial.
- Análise, inventário e matriz completa: projeto irmão megalodonte-android/docs.
- Instrumentação Android de widgets/tema/descarte passou em emulador API 37;
  API 35/rotação/layouts avançados ainda exigem validação específica.
- Novos core/portable são pastas do umbrella nesta etapa; remotos/submódulos futuros.

As notas de dependência anteriores abaixo registram a estrutura pré-extração.

## Estrutura
- JavaFX + Megalodonte (UI framework)
- modular: cada módulo (megalodonte-base, megalodonte-reactivity, megalodonte-router, megalodonte-theme, megalodonte-components) é um submodulo git independente, publicado no Maven Local via `gradlew build publishToMavenLocal`
- dependências compiladas sequencialmente na ordem: base → reactivity → router → theme → components

## Dependências entre módulos
- megalodonte-base: módulo raiz — contém interfaces de reatividade (ReadableState, State, ForEachState) e Component
- megalodonte-reactivity: depende de base, fornece ComputedState, ListState, ListenerManager, Show
- megalodonte-components: depende APENAS de base (não depende mais de reactivity)
- megalodonte-theme: depende de base
- megalodonte-router: depende de base

## Última alteração
- Router v5 (`megalodonte-router`) reduzido a navegação pura
- `megalodonte-base` passa a guardar as rotas (`RouteTable`) e o lifecycle de telas (`ScreenManager`)
- `spawnWindow` implementado em `megalodonte.base.route.v2.ScreenContextBase`
- `RouterBase` sem `spawnWindow` no contrato (spawn fica no ScreenContext)

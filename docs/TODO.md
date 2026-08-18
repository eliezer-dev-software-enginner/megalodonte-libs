# TODO

## Concluído
- [x] `megalodonte.base.async.Scope` (cancelamento de trabalho assíncrono vinculado ao ciclo de
      vida) — `ScopeTest` (7 casos), `./gradlew test`: BUILD SUCCESSFUL. Ver `DECISIONS.md`.
- [x] `megalodonte-base` e `megalodonte-router` publicados em `mavenLocal`
      (`publishToMavenLocal`); `ScreenContext.scope()` + `Router` cancelando automaticamente nos
      3 pontos de `onDestroy()`. Validado ponta a ponta contra `balanca-gobitech`
      (`--refresh-dependencies compileJava test`: 155/155). Ver `DECISIONS.md`.
- [x] Fase 3: `PesagemViewModel` (`balanca-gobitech`) migrado da flag `destruido` manual pra
      `ctx.scope()` — primeiro uso real da API fora do framework. 155/155 testes sem regressão.
- [x] Fase 4: auditado `plics-sw` inteiro (e os outros apps do ecossistema) atrás do mesmo padrão
      de risco. Achado um caso real (`HomeScreenViewModel.executor` nunca desligado) — não era a
      mesma corrida (recurso síncrono, não assíncrono), então corrigido direto com
      `shutdownNow()` em vez de `Scope`. Ver `DECISIONS.md` do `plics-sw`. 294/294 testes sem
      regressão.

- [x] Simplificar Theme.java (herda ThemeInterface, sem redeclarações)
- [x] Corrigir DefaultTheme.java (7 args colors, 4 args border, sem radius())
- [x] Remover métodos button*() de ThemeManager.java
- [x] Substituir ThemeManager.button*() por constantes locais em ButtonProps
- [x] Substituir theme.radius().md() por theme.border().radiusMd() em todos os componentes
- [x] Atualizar testes (DefaultThemeTest, ThemeManagerTest)
- [x] Compilação bem-sucedida (base + theme + components)

- [x] Refatorar Context.useRouter: bind + retorna RouterBuilder; start() é o método terminal que configura a view

- [x] Mover ForEachState de megalodonte-reactivity para megalodonte-base
- [x] Remover dependência de megalodonte-reactivity de megalodonte-components
- [x] Atualizar imports nos componentes (LayoutComponent, Row, Column usam megalodonte.base.state.ForEachState)

- [x] SelectProps com estilização inline + tema (como InputProps)

## Pendências
- Nenhuma pendência conhecida do plano de cancelamento estruturado (Fases 1-4 concluídas).

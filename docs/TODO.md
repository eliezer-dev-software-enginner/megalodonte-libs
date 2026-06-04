# TODO

## Concluído
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

## Pendências
- Nenhuma pendência conhecida

# Decisões Arquiteturais

## 2026-06-03 — Alinhamento à ThemeInterface simplificada

**Problema**: ThemeInterface foi simplificada em megalodonte-base (removido `radius()` e `ThemeRadius`, mesclado em `ThemeBorder`), mas megalodonte-theme e megalodonte-components ainda usavam a API antiga.

**Decisão**:
1. Theme.java: removida declaração redundante de métodos e `radius()` — herda tudo de ThemeInterface
2. DefaultTheme.java: colors() usa ThemeColors base (7 args); border() usa 4 args; removido radius()
3. ThemeManager.java: removidos métodos convenience button*() (cores movidas para ButtonProps)
4. Componentes: theme.radius().md() → theme.border().radiusMd()
5. Cores de botão: movidas de ThemeColors para constantes locais em ButtonProps

**Motivo**: Alinhar os módulos downstream à interface simplificada da base, eliminando código morto e erros de compilação.

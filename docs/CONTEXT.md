# Contexto do Projeto

## Estrutura
- JavaFX + Megalodonte (UI framework)
- modular: base → theme → components

## Última alteração
- Alinhado megalodonte-theme e megalodonte-components à ThemeInterface simplificada
- Theme.java agora é apenas `interface Theme extends ThemeInterface {}`
- DefaultTheme.java: colors() usa 7 args, border() usa 4 args (radiusSm/Md/Lg), removido radius()
- ThemeManager.java: removidos métodos button*()
- Componentes: theme.radius().md() → theme.border().radiusMd()
- ButtonProps: cores de botão definidas como constantes locais

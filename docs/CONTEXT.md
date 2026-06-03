# Contexto do Projeto

## Estrutura
- JavaFX + Megalodonte (UI framework)
- modular: base → theme → components

## Última alteração
- Removido Theme.java (interface marker vazia) e ThemeColors.java (morto)
- ThemeManager agora trabalha com ThemeInterface (qualquer impl pode ser registrada)
- Componentes usam ThemeInterface em vez de Theme em todas as assinaturas
- PropsInterface.java simplificado: removido applyTheme do contrato
- Props.java limpo: removido dual applyTheme e código comentado

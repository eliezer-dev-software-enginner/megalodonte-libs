# Contexto do Projeto

## Estrutura
- JavaFX + Megalodonte (UI framework)
- modular: cada módulo (megalodonte-base, megalodonte-reactivity, megalodonte-router, megalodonte-theme, megalodonte-components) é um submodulo git independente, publicado no Maven Local via `gradlew build publishToMavenLocal`
- dependências compiladas sequencialmente na ordem: base → reactivity → router → theme → components

## Última alteração
- Removido Theme.java (interface marker vazia) e ThemeColors.java (morto)
- ThemeManager agora trabalha com ThemeInterface (qualquer impl pode ser registrada)
- Componentes usam ThemeInterface em vez de Theme em todas as assinaturas
- PropsInterface.java simplificado: removido applyTheme do contrato
- Props.java limpo: removido dual applyTheme e código comentado

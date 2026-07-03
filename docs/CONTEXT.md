# Contexto do Projeto

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
- SelectProps refatorado para estender TextComponentProps (herdando fontSize, fontWeight, textColor)
- Adicionados bgColor, borderColor, borderWidth, borderRadius, tone, variant, disable com fallback ao tema via StyleUtils
- SelectProps agora suporta estilização inline + tema, mesmo padrão de InputProps

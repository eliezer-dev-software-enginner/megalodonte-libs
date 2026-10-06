# megalodonte-core

Contrato neutro compartilhado por JavaFX e Android. Contém estado observável,
assinaturas descartáveis, tokens/ThemeInterface/ThemeManager, política de escala,
ScreenLifecycle, matcher de rotas e BackendContract v1.

```kotlin
implementation("megalodonte:megalodonte-core:1.0.0-beta")
```

Compilado com JDK 25 e `--release 17`. O check bloqueia imports de plataforma e
bytecode superior a Java 17. O módulo JPMS `megalodonte.core` exporta os pacotes
neutros originais; os hosts não podem recompilar esses mesmos tipos em seus jars.

```powershell
./gradlew.bat check publishToMavenLocal
```

`ScaleProvider.setDetector` e `ThemePlatform.install` pertencem ao bootstrap de
cada host. Fonts/scene styling continuam específicos; os hooks não afirmam que
uma Scene existe em Android. `ThemeManager.applyFontFamily(Object)` é transitório:
consumidores binários antigos devem ser recompilados.

```java
try (Subscription binding = state.observe(this::update)) {
    // Encerrar o handle remove a assinatura do estado.
}
```

`map` observa a fonte somente enquanto há assinantes e mantém supressão de valores
derivados iguais. Estados/observadores devem ser usados na thread definida pelo
host; volatile não torna listas de listeners thread-safe.
Mapeadores devem ser puros: get do estado derivado recalcula a partir da fonte;
ComputedState continua sendo a opção de cálculo cacheado por dependências.

O contrato de capacidade descreve categorias, não toda a API dos componentes.
Consulte a análise/matriz no projeto irmão `megalodonte-android/docs` para lacunas
e sequência de migração. Novos artefatos permanecem no repo umbrella nesta etapa.

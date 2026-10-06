# megalodonte-reactivity-portable

Projeção publicável dos mesmos fontes neutros de megalodonte-reactivity:
ComputedState, ListenerManager, ForEachState e v2.ListState. Não copia nem mantém
uma segunda implementação. A preparação de fontes é gerada em build pelo Gradle.

Depende somente de megalodonte-core e gera bytecode Java 17. Não inclui Show,
exemplos ou module-info JavaFX.

```kotlin
implementation("megalodonte:megalodonte-reactivity-portable:1.0.0-beta")
```

Não combinar com o jar de reactivity desktop no mesmo app: existem classes com
os mesmos nomes. O backend Android fornece seu próprio Show. ComputedState e
ForEachState possuem close para descartar observação de suas dependências.

```powershell
./gradlew.bat check publishToMavenLocal
```

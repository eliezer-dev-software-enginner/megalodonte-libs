# megalodonte-libs

## Multiplataforma

`megalodonte-core` contém estado, tema/tokens/escala e contratos neutros, usados
por JavaFX e Android. `megalodonte-theme` depende somente dele e pode ser declarado
no Gradle de um cliente Android. `megalodonte-reactivity-portable` compila os mesmos
fontes neutros de reactivity, excluindo seu Show JavaFX.

Os novos artefatos usam toolchain Java 25 com bytecode release 17. O backend
desktop continua Java 25. `install-all` publica os contratos antes dos consumidores.
Recompilar consumidores após a extração; consulte docs/DECISIONS.md.
Core/portable pertencem ao umbrella nesta etapa, ainda sem repos/submódulos próprios.

## clone all the submodules

```bash
git clone --recurse-submodules https://github.com/eliezer-dev-software-enginner/megalodonte-libs.git
```

## Enter on `megalodonte-libs`

```bash
cd megalodonte-libs
```

## run on linux

```bash
./install-all.sh
```

## run on windows

```bash
./install-all.ps1
```

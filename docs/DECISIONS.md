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

## 2026-06-03 — Remoção do marker Theme.java e uso direto de ThemeInterface

**Problema**: `Theme.java` era uma interface vazia (`interface Theme extends ThemeInterface {}`) que não agregava valor, mas criava acoplamento — `ThemeManager` só aceitava `Theme`, impedindo que implementações diretas de `ThemeInterface` fossem registradas.

**Decisão**:
1. Removido `Theme.java` (interface marker vazia)
2. Removido `ThemeColors.java` (14 campos, morto) e seu teste
3. `ThemeManager` agora trabalha com `ThemeInterface` — qualquer implementação pode ser registrada
4. Componentes agora usam `ThemeInterface` em vez de `Theme` em todas as assinaturas
5. `PropsInterface.java` simplificado: removido `applyTheme` do contrato (nunca era usado de fato — o método real é o abstrato em `Props`)
6. `Props.java` limpo: removido dual `applyTheme` e código comentado

**Flexibilidade**: qualquer biblioteca de tema externa faz:
```java
public class MyTheme implements ThemeInterface { ... }
ThemeManager.setTheme(new MyTheme());
```
Sem depender de interface fantasma.

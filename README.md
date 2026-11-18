# Nova Client

Mod de Fabric (solo cliente) para **Nova PX Event Launcher**. Menú dentro del juego con **Shift derecho**.

Módulos: FPS, CPS, coordenadas, teclas (WASD + espacio), armadura/durabilidad y zoom (mantén **C**).

## Compilar
Lo hace GitHub Actions en cada push (descarga el `.jar` desde la pestaña *Actions*). Al subir un tag `vX.Y.Z` se publica una release con el `.jar`.
En local: `gradle build` (Java 21) → `build/libs/`.

## Añadir un módulo
1. Crea una clase en `src/main/java/com/novapx/client/modules/` que extienda `Module`.
2. Regístrala en `ModuleManager`.

## Versiones de Minecraft
Esta rama es **1.21.1**. Para otra versión se cambian `minecraft_version`, `yarn_mappings` y `fabric_version` en `gradle.properties` (y se ajusta lo que haya cambiado en la API).

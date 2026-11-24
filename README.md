<div align="center">

# Nova Client

**Cliente ligero para Minecraft (Fabric) hecho por el equipo de Nova PX.**<br />
Menú dentro del juego y módulos de HUD, para **36 versiones** de Minecraft: la 1.15.2 y de la 1.16.5 a la 26.3.

[![Compilación](https://img.shields.io/github/actions/workflow/status/javierroman94/nova-client/build.yml?style=for-the-badge&labelColor=0e1220&label=build)](https://github.com/javierroman94/nova-client/actions)
[![Última versión](https://img.shields.io/github/v/release/javierroman94/nova-client?style=for-the-badge&color=f2b84b&labelColor=0e1220&label=versi%C3%B3n)](https://github.com/javierroman94/nova-client/releases/latest)
[![Licencia MIT](https://img.shields.io/badge/licencia-MIT-5fd38d?style=for-the-badge&labelColor=0e1220)](LICENSE)
![Fabric](https://img.shields.io/badge/Fabric-1.15.2%20%E2%86%92%2026.3-4cc9f0?style=for-the-badge&labelColor=0e1220)

### [⬇️ Descargar la última versión](https://github.com/javierroman94/nova-client/releases/latest)

Pensado para usarse con el **[Nova PX Event Launcher](https://github.com/javierroman94/Nova-PX-Event-Launcher)**.

</div>

---

## ✨ Qué incluye

Abre el menú con **Shift derecho** y activa o desactiva cada módulo. Tu elección se guarda en `config/nova-client.json`.

| Módulo | Qué hace |
|---|---|
| **FPS** | Fotogramas por segundo. |
| **CPS** | Clics por segundo (izquierdo \| derecho), contados cada tick, sin depender de los FPS. |
| **Coordenadas** | Posición y hacia dónde miras. |
| **Teclas** | W A S D y espacio en pantalla; se iluminan al pulsarlas. |
| **Armadura** | Tu armadura y el objeto en mano con su durabilidad. |
| **Zoom** | Mantén **C** para acercar la vista; al soltar vuelve tu FOV. |

El HUD se oculta solo con F1 y con el menú de depuración (F3).

## 🎨 Texture pack Nova

Un texture pack propio para que los menús combinen con el cliente: **azul noche con acento cian**, botones con brillo al pasar el ratón, hotbar con selector luminoso, mira cian, inventario y cofres en panel claro (el texto sigue leyéndose) y fondo de menús con estrellas.

- **Un solo archivo para todas las versiones:** `Nova-Pack.zip` (en cada [release](https://github.com/javierroman94/nova-client/releases/latest)). Funciona de la 1.15.2 a la 26.x.
- **Instalación:** ponlo en la carpeta `resourcepacks` y actívalo en *Opciones → Paquetes de recursos*. Si el juego avisa «hecho para otra versión», se puede usar igual.
- Cambia: botones, hotbar, mira, inventario, mesa de crafteo, cofres, shulker, dispensador y fondos de menú. No toca los objetos ni los bloques.
- Se genera con `python3 tools/gen_pack.py` (necesita Pillow); los dibujos son propios, no copian los de Minecraft.

## 📥 Instalación

1. Instala [Fabric Loader](https://fabricmc.net/use/) y el [Fabric API](https://modrinth.com/mod/fabric-api) de tu versión (en 26.x hace falta Java 25).
2. Descarga de [Releases](https://github.com/javierroman94/nova-client/releases/latest) el `.jar` que coincide con tu versión de Minecraft.
3. Ponlo en la carpeta `mods` de tu perfil.

Si usas el Nova PX Event Launcher, crea un perfil **Nova** y el launcher instala el Nova Client por ti.

## 🎮 Versiones compatibles

| Minecraft | Java | Archivo |
|---|---|---|
| 1.15.2 | Java 8 | `nova-client-0.5.1+mc1.15.2.jar` |
| 1.16.5 | Java 8 | `nova-client-0.5.1+mc1.16.5.jar` |
| 1.17 | Java 16 | `nova-client-0.5.1+mc1.17.jar` |
| 1.17.1 | Java 16 | `nova-client-0.5.1+mc1.17.1.jar` |
| 1.18 | Java 17 | `nova-client-0.5.1+mc1.18.jar` |
| 1.18.1 | Java 17 | `nova-client-0.5.1+mc1.18.1.jar` |
| 1.18.2 | Java 17 | `nova-client-0.5.1+mc1.18.2.jar` |
| 1.19 | Java 17 | `nova-client-0.5.1+mc1.19.jar` |
| 1.19.1 | Java 17 | `nova-client-0.5.1+mc1.19.1.jar` |
| 1.19.2 | Java 17 | `nova-client-0.5.1+mc1.19.2.jar` |
| 1.19.3 | Java 17 | `nova-client-0.5.1+mc1.19.3.jar` |
| 1.19.4 | Java 17 | `nova-client-0.5.1+mc1.19.4.jar` |
| 1.20 | Java 17 | `nova-client-0.5.1+mc1.20.jar` |
| 1.20.1 | Java 17 | `nova-client-0.5.1+mc1.20.1.jar` |
| 1.20.2 | Java 17 | `nova-client-0.5.1+mc1.20.2.jar` |
| 1.20.3 | Java 17 | `nova-client-0.5.1+mc1.20.3.jar` |
| 1.20.4 | Java 17 | `nova-client-0.5.1+mc1.20.4.jar` |
| 1.20.5 | Java 21 | `nova-client-0.5.1+mc1.20.5.jar` |
| 1.20.6 | Java 21 | `nova-client-0.5.1+mc1.20.6.jar` |
| 1.21 | Java 21 | `nova-client-0.5.1+mc1.21.jar` |
| 1.21.1 | Java 21 | `nova-client-0.5.1+mc1.21.1.jar` |
| 1.21.2 | Java 21 | `nova-client-0.5.1+mc1.21.2.jar` |
| 1.21.3 | Java 21 | `nova-client-0.5.1+mc1.21.3.jar` |
| 1.21.4 | Java 21 | `nova-client-0.5.1+mc1.21.4.jar` |
| 1.21.5 | Java 21 | `nova-client-0.5.1+mc1.21.5.jar` |
| 1.21.6 | Java 21 | `nova-client-0.5.1+mc1.21.6.jar` |
| 1.21.7 | Java 21 | `nova-client-0.5.1+mc1.21.7.jar` |
| 1.21.8 | Java 21 | `nova-client-0.5.1+mc1.21.8.jar` |
| 1.21.9 | Java 21 | `nova-client-0.5.1+mc1.21.9.jar` |
| 1.21.10 | Java 21 | `nova-client-0.5.1+mc1.21.10.jar` |
| 1.21.11 | Java 21 | `nova-client-0.5.1+mc1.21.11.jar` |
| 26.1 | Java 25 | `nova-client-0.5.1+mc26.1.jar` |
| 26.1.1 | Java 25 | `nova-client-0.5.1+mc26.1.1.jar` |
| 26.1.2 | Java 25 | `nova-client-0.5.1+mc26.1.2.jar` |
| 26.2 | Java 25 | `nova-client-0.5.1+mc26.2.jar` |
| 26.3 | Java 25 | `nova-client-0.5.1+mc26.3.jar` |

Cada versión se compila por separado en GitHub Actions (pestaña *Actions*) y al subir una etiqueta `vX.Y.Z` se publican todos los `.jar` en una sola release.

## 🛠️ Para desarrolladores

Requisitos: Java 21 y Gradle 9.2 o superior.

```bash
gradle build                                   # compila la versión de gradle.properties
gradle build -Pminecraft_version=1.20.4 \
  -Pyarn_mappings=1.20.4+build.3 -Pfabric_version=0.97.3+1.20.4 \
  -Pjava_version=17 -Pmc_range="~1.20.4" -Pcompat=base
```

Los parámetros de cada versión están en [`versions.json`](versions.json), que también usa la compilación automática.

### Añadir un módulo
1. Crea una clase en `src/main/java/com/novapx/client/modules/` que extienda `Module`.
2. Regístrala en `ModuleManager`.

### Diferencias entre versiones
El código es común; lo que cambia entre versiones de Minecraft vive en clases pequeñas por «familia» (`src/fam-*`) y en una clase `Compat` con variantes en `src/compat/` (1.20 en adelante). Las versiones 26.x usan nombres oficiales de Mojang y tienen su propio proyecto en [`mj26/`](mj26) (Gradle 9.8 y Java 25).

| Familia | Versiones |
|---|---|
| `ms15` | 1.15.2 |
| `ms16` | 1.16.5 – 1.17 |
| `ms17` / `ms18` / `ms180` | 1.17.1 – 1.18.2 |
| `ms19` / `ms193` / `ms194` | 1.19 – 1.19.4 |
| `dc` + `compat` | 1.20 – 1.21.11 |
| `mj26` (a/b) | 26.1 – 26.3 |

## 📄 Licencia

[MIT](LICENSE). Nova Client no está afiliado a Mojang Studios ni a Microsoft.

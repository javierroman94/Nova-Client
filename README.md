<div align="center">

# Nova Client

**Cliente ligero para Minecraft (Fabric) hecho por el equipo de Nova PX.**<br />
Menú dentro del juego y módulos de HUD, para **19 versiones** de Minecraft, de la 1.20 a la 1.21.11.

[![Compilación](https://img.shields.io/github/actions/workflow/status/javierroman94/nova-client/build.yml?style=for-the-badge&labelColor=0e1220&label=build)](https://github.com/javierroman94/nova-client/actions)
[![Última versión](https://img.shields.io/github/v/release/javierroman94/nova-client?style=for-the-badge&color=f2b84b&labelColor=0e1220&label=versi%C3%B3n)](https://github.com/javierroman94/nova-client/releases/latest)
[![Licencia MIT](https://img.shields.io/badge/licencia-MIT-5fd38d?style=for-the-badge&labelColor=0e1220)](LICENSE)
![Fabric](https://img.shields.io/badge/Fabric-1.20%20%E2%86%92%201.21.11-4cc9f0?style=for-the-badge&labelColor=0e1220)

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

## 📥 Instalación

1. Instala [Fabric Loader](https://fabricmc.net/use/) y el [Fabric API](https://modrinth.com/mod/fabric-api) de tu versión.
2. Descarga de [Releases](https://github.com/javierroman94/nova-client/releases/latest) el `.jar` que coincide con tu versión de Minecraft.
3. Ponlo en la carpeta `mods` de tu perfil.

Si usas el Nova PX Event Launcher, créate un perfil Fabric y suelta el `.jar` en su pestaña de mods.

## 🎮 Versiones compatibles

| Minecraft | Java | Archivo |
|---|---|---|
| 1.20 | Java 17 | `nova-client-0.2.0+mc1.20.jar` |
| 1.20.1 | Java 17 | `nova-client-0.2.0+mc1.20.1.jar` |
| 1.20.2 | Java 17 | `nova-client-0.2.0+mc1.20.2.jar` |
| 1.20.3 | Java 17 | `nova-client-0.2.0+mc1.20.3.jar` |
| 1.20.4 | Java 17 | `nova-client-0.2.0+mc1.20.4.jar` |
| 1.20.5 | Java 21 | `nova-client-0.2.0+mc1.20.5.jar` |
| 1.20.6 | Java 21 | `nova-client-0.2.0+mc1.20.6.jar` |
| 1.21 | Java 21 | `nova-client-0.2.0+mc1.21.jar` |
| 1.21.1 | Java 21 | `nova-client-0.2.0+mc1.21.1.jar` |
| 1.21.2 | Java 21 | `nova-client-0.2.0+mc1.21.2.jar` |
| 1.21.3 | Java 21 | `nova-client-0.2.0+mc1.21.3.jar` |
| 1.21.4 | Java 21 | `nova-client-0.2.0+mc1.21.4.jar` |
| 1.21.5 | Java 21 | `nova-client-0.2.0+mc1.21.5.jar` |
| 1.21.6 | Java 21 | `nova-client-0.2.0+mc1.21.6.jar` |
| 1.21.7 | Java 21 | `nova-client-0.2.0+mc1.21.7.jar` |
| 1.21.8 | Java 21 | `nova-client-0.2.0+mc1.21.8.jar` |
| 1.21.9 | Java 21 | `nova-client-0.2.0+mc1.21.9.jar` |
| 1.21.10 | Java 21 | `nova-client-0.2.0+mc1.21.10.jar` |
| 1.21.11 | Java 21 | `nova-client-0.2.0+mc1.21.11.jar` |

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
El código es común; lo que cambia entre versiones de Minecraft vive en una clase `Compat` con cuatro variantes en `src/compat/`:

| Variante | Versiones | Qué cambia |
|---|---|---|
| `legacy` | 1.20 – 1.20.1 | Menú de depuración (`debugEnabled`). |
| `base` | 1.20.2 – 1.21.1 | API de referencia. |
| `modern` | 1.21.2 – 1.21.8 | Dibujo de la durabilidad (`drawStackOverlay`). |
| `modern2` | 1.21.9 – 1.21.11 | Categorías de teclas (`KeyBinding.Category`). |

## 📄 Licencia

[MIT](LICENSE). Nova Client no está afiliado a Mojang Studios ni a Microsoft.

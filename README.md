# Fundación Textil Circular — App móvil

Trabajo semestral de la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles** (Duoc UC, sede Viña del Mar, sección 006D).

**Equipo:** Alfonzo Pizarro, Benjamín Martínez y Bruno Mateluna.

## De qué trata

La [Fundación Textil Circular](https://www.textilcircular.cl/) mantiene un directorio de más de 980 emprendedoras textiles de las 16 regiones de Chile (segunda mano, arriendo, reparación, suprarreciclaje y talleres). Hoy el directorio funciona con formularios de Google, no muestra fotos y obliga a las emprendedoras a depender de Instagram.

Esta app permite que las emprendedoras gestionen su perfil y su catálogo con fotos, que los clientes encuentren sus trabajos y se comuniquen con ellas dentro de la plataforma, y que la administración valide cuentas y vea métricas de uso.

## Estado actual

Está creada la **base de la app**: un proyecto nuevo de Android Studio (plantilla *Empty Activity*) con Jetpack Compose y Material 3. Todavía **no hay** pantallas, navegación, base de datos local ni conexión al servidor: cada funcionalidad se incorpora por historia de usuario, en su propia rama.

La planificación del equipo (historias, responsables y avance) se lleva en **Trello**.

## Tecnologías

- Kotlin, Android Studio, Jetpack Compose y Material Design 3
- Android 9 o superior (`minSdk` 28)
- Previstas: Navigation Compose, arquitectura MVVM, Room (datos sin conexión), Retrofit y un servidor en Spring Boot (API REST)

## Cómo abrir y ejecutar la app

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Bruno-M-M/DUOCUC_MOVIL_2026_VINA_006D_AlfonzoPizarro-BenjaminMartinez-BrunoMateluna.git
   ```
2. En Android Studio, abre la carpeta **`android`** (no la carpeta de más afuera). Es la que contiene `settings.gradle.kts`.
3. Espera a que termine el *Sync* de Gradle. La primera vez necesita internet.
4. Ejecuta con *Run*, en un emulador o en un celular.

## Estructura del repositorio

```
android/   proyecto de la app (Android Studio)
README.md  este archivo
```

Dentro de `android/app/src/main/java/com/fundaciontextil/`:

| Carpeta o archivo | Qué contiene |
|---|---|
| `MainActivity.kt` | Punto de entrada de la app |
| `ui/theme/` | Colores, tipografía y tema Material 3 (por ahora los de la plantilla) |

## Normas del repositorio

- Cada integrante configura en git el correo de **su propia cuenta de GitHub**, para que sus commits queden a su nombre.
- Una rama por tarea, por ejemplo `feature/HU-14-chat`, y commits con mensaje claro (`feat:`, `fix:`, `docs:`).
- No se hace `git init` dentro de ninguna subcarpeta.
- Este repositorio es **público**. Nunca se suben contraseñas, la llave de firma del APK ni datos personales reales; las pruebas usan solo datos ficticios o anonimizados (Ley N.º 21.719 y Ley N.º 19.628).

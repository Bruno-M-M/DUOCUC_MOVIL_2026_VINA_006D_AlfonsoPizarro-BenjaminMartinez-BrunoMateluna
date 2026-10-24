# Fundación Textil Circular — App móvil

Trabajo semestral de la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles** (Duoc UC, sede Viña del Mar, sección 006D).

**Equipo:** Alfonzo Pizarro, Benjamín Martínez y Bruno Mateluna.

## De qué trata

La [Fundación Textil Circular](https://www.textilcircular.cl/) mantiene un directorio de más de 980 emprendedoras textiles de las 16 regiones de Chile (segunda mano, arriendo, reparación, suprarreciclaje y talleres). Hoy el directorio funciona con formularios de Google, no muestra fotos y obliga a las emprendedoras a depender de Instagram.

Esta app permite que las emprendedoras gestionen su perfil y su catálogo con fotos, que los clientes encuentren sus trabajos y se comuniquen con ellas dentro de la plataforma, y que la administración valide cuentas y vea métricas de uso.

## Estado actual

Está creada la **base de la app**: un proyecto de Android Studio (plantilla *Empty Activity*) con Jetpack Compose y Material 3, y la **estructura MVVM** lista, con una pantalla de ejemplo, su ViewModel, una capa de repositorio, un `ServiceLocator` y una prueba unitaria. La pantalla de ejemplo es temporal: se reemplaza por las pantallas reales. Todavía **no hay** navegación, base de datos local (Room) ni conexión al servidor: cada funcionalidad se incorpora por historia de usuario, en su propia rama.

La planificación del equipo (historias, responsables y avance) se lleva en [**Trello**](https://trello.com/b/kFvJRnCK/federaciontextil).

## Tecnologías

- Kotlin, Android Studio, Jetpack Compose y Material Design 3
- Android 9 o superior (`minSdk` 28)
- Arquitectura MVVM con `StateFlow` y corrutinas
- Dependencias ya agregadas: Navigation Compose, ViewModel para Compose, Room (con KSP) y `kotlinx-coroutines-test` para las pruebas
- Previstas: Retrofit y un servidor en Spring Boot (API REST)

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
| `data/ServiceLocator.kt` | Arma en un solo lugar los repositorios (y, más adelante, la base de datos) y se los entrega a los ViewModels, sin librería de inyección |
| `data/repository/` | Interfaces de los repositorios y sus implementaciones. Es la única puerta entre los ViewModels y los datos |
| `ui/<sección>/` | Una carpeta por sección de la app, con su pantalla, su ViewModel y su estado. Hoy solo existe `ui/ejemplo/` |
| `ui/theme/` | Colores, tipografía y tema Material 3 (por ahora los de la plantilla) |

Las carpetas `data/local/` (Room), `ui/navigation/` y `ui/components/` se crean junto con las tarjetas que las necesitan.

## Arquitectura

La app sigue **MVVM**: cada capa conoce solo a la que tiene debajo.

```
Pantalla (Compose) ─→ ViewModel ─→ Repositorio (interfaz) ─→ datos (hoy en memoria; luego Room)
```

| Capa | Responsabilidad | Ejemplo en el código |
|---|---|---|
| **Pantalla** | Solo dibuja el estado y avisa cuando el usuario toca algo. No decide nada | `EjemploScreen` |
| **Estado (`UiState`)** | Un `data class` con todo lo que la pantalla necesita mostrar | `EjemploUiState` |
| **ViewModel** | Guarda el estado, contiene la lógica y las validaciones, y sobrevive a la rotación de la pantalla | `EjemploViewModel` |
| **Repositorio** | Decide de dónde salen los datos. El ViewModel depende de la interfaz, no de la implementación | `PulsacionesRepository` |
| **ServiceLocator** | Crea los repositorios una sola vez y los entrega a los ViewModels mediante una *factory* | `ServiceLocator` |

Reglas del código:

- El `MutableStateFlow` es privado en el ViewModel; la pantalla recibe una versión de solo lectura (`StateFlow`).
- Las validaciones viven en el ViewModel, no en la pantalla, para poder probarlas sin dibujar nada.
- Las funciones que acceden a datos son `suspend` y se lanzan con `viewModelScope`.

## Pruebas

Las pruebas unitarias se ejecutan en el computador, sin emulador:

```bash
./gradlew testDebugUnitTest
```

En Windows, `gradlew.bat testDebugUnitTest`, desde la carpeta `android`. Los ViewModels se prueban con un repositorio en memoria y `kotlinx-coroutines-test`; el ejemplo está en `app/src/test/.../ui/ejemplo/EjemploViewModelTest.kt`.

## Cómo trabajamos

Las tareas se planifican en [Trello](https://trello.com/b/kFvJRnCK/federaciontextil): cada tarjeta es una historia de usuario o una tarea técnica, con su responsable y su checklist. El código sigue un flujo simple, de una rama por tarjeta.

**Ramas**

- `main` siempre compila y es la versión que se podría entregar. Nadie hace commit directo en ella.
- Cada tarjeta tiene su propia rama, creada desde un `main` actualizado y con el mismo nombre que la tarjeta: `feature/HU-37-registro-cliente`, `fix/login-clave-vacia`, `docs/readme-ejecucion`.
- Las ramas duran pocos días. Antes de empezar una tarjeta y a diario mientras se trabaja: `git pull` en `main` y traer los cambios a la rama.

```bash
git switch main
git pull
git switch -c feature/HU-37-registro-cliente
```

**Commits**

- Cada integrante configura en git el correo de **su propia cuenta de GitHub**, para que sus commits queden a su nombre.
- Commits pequeños, con un mensaje técnico que explique qué cambia y por qué, y un prefijo: `feat:` (funcionalidad), `fix:` (corrección), `refactor:`, `test:` o `docs:`. Por ejemplo: `feat: validar el RUT con dígito verificador en el registro`.

**Pull requests**

1. Al terminar, se sube la rama y se abre un *pull request* hacia `main`. El título lleva la tarjeta: «HU-37 · Registro de cliente».
2. **Otra persona del equipo** lo revisa y lo aprueba. Nadie une su propio PR.
3. Se une con *Create a merge commit* o *Rebase and merge* (no *Squash*), para que cada commit conserve su autor.
4. Se borra la rama y la tarjeta pasa a *Hecho* en Trello.

**Archivos que toca todo el equipo**

`libs.versions.toml`, las rutas y la navegación, y el componente `Campo` se unen a `main` primero y rápido, en cambios pequeños. Así los demás parten de ahí y se evitan conflictos.

**Cierre de la entrega**

Hay una fecha de congelamiento acordada antes de la entrega. Pasada la fecha límite no se sube nada más al repositorio.

## Normas del repositorio

- No se hace `git init` dentro de ninguna subcarpeta.
- Este repositorio es **público**. Nunca se suben contraseñas, la llave de firma del APK ni datos personales reales; las pruebas usan solo datos ficticios o anonimizados (Ley N.º 21.719 y Ley N.º 19.628).

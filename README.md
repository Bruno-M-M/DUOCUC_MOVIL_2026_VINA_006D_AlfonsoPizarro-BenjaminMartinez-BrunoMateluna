# Fundación Textil Circular — App móvil

Trabajo semestral de la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles** (Duoc UC, sede Viña del Mar, sección 006D).

**Equipo:** Alfonzo Pizarro, Benjamín Martínez y Bruno Mateluna.

## De qué trata

La [Fundación Textil Circular](https://www.textilcircular.cl/) mantiene un directorio de más de 980 emprendedoras textiles de las 16 regiones de Chile (segunda mano, arriendo, reparación, suprarreciclaje y talleres). Hoy el directorio funciona con formularios de Google, no muestra fotos y obliga a las emprendedoras a depender de Instagram.

Esta app permite que las emprendedoras gestionen su perfil y su catálogo con fotos, que los clientes encuentren sus trabajos y se comuniquen con ellas dentro de la plataforma, y que la administración valide cuentas y vea métricas de uso.

## Estado actual

La app funciona con **datos locales** (Room), sin servidor todavía. Lo que ya está hecho:

- **Acceso:** pantalla de inicio (*splash*) que recuerda la sesión, inicio de sesión, registro de cliente, registro de emprendedora (con su emprendimiento, región, comuna y hasta dos categorías) y recuperar contraseña. Todos los formularios validan en vivo: correo, RUT con dígito verificador, contraseña segura y aceptación de términos.
- **Perfil:** cada persona edita su nombre, celular, región y comuna, y agrega una **foto de perfil** con la cámara o desde la galería.
- **Productos:** la emprendedora publica productos con nombre, descripción, precio y foto, y los ve en su catálogo.
- **Navegación:** barra inferior con Explorar, Mensajes, Novedades y Perfil, y modo invitado.

La planificación del equipo (historias, responsables y avance) se lleva en [**Trello**](https://trello.com/b/kFvJRnCK/federaciontextil).

## Limitaciones de esta entrega

- **Recuperar contraseña** valida el correo y muestra siempre el mismo mensaje («si el correo está registrado, te enviaremos las instrucciones»), exista o no la cuenta, para no revelar qué correos están registrados. El envío real del correo depende del servidor, que se incorpora en la siguiente etapa; por ahora el envío es simulado.
- **Explorar, Mensajes y Novedades**, y los detalles de emprendedora, producto y conversación, son por ahora pantallas de navegación sin contenido real: se completan en la siguiente etapa.
- Los datos se guardan solo en el teléfono. La conexión al servidor (Retrofit y API REST) se incorpora en la siguiente etapa.

## Tecnologías

- Kotlin, Android Studio, Jetpack Compose y Material Design 3
- Android 9 o superior (`minSdk` 28)
- Arquitectura MVVM con `StateFlow` y corrutinas
- Navigation Compose para la navegación entre pantallas
- Room para la base de datos local, y `SharedPreferences` para recordar la sesión
- Contraseñas guardadas con hash PBKDF2 y sal, nunca en texto plano
- Cámara y galería con `ActivityResultContracts` y un `FileProvider`, sin permisos peligrosos
- Pruebas con JUnit, `kotlinx-coroutines-test` y Compose UI Test
- Previstos: Retrofit y un servidor en Spring Boot (API REST)

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
| `MainActivity.kt`, `TextilApp.kt` | Punto de entrada de la app y su inicio (limpieza de fotos que ya no se usan) |
| `data/ServiceLocator.kt` | Arma en un solo lugar la base de datos y los repositorios y se los entrega a los ViewModels, sin librería de inyección |
| `data/local/` | Base de datos Room: entidades, DAO y datos iniciales del catálogo |
| `data/model/` | Clases de datos de la app (`Usuario`, `Rol`, regiones y comunas, resultados) |
| `data/repository/` | Repositorios: la única puerta entre los ViewModels y los datos |
| `data/seguridad/` | Hash de contraseñas y RUT |
| `data/foto/` | Guardado y reducción de las fotos tomadas con la cámara o elegidas en la galería |
| `ui/auth/` | Inicio de sesión, registros, recuperar contraseña y sus validaciones |
| `ui/perfil/` | Perfil editable y foto de perfil |
| `ui/producto/` | Publicar un producto y catálogo de la emprendedora |
| `ui/foto/` | Controles compartidos para tomar o elegir una foto |
| `ui/splash/` | Pantalla de inicio |
| `ui/navigation/` | Rutas y navegación con barra inferior |
| `ui/components/` | Componentes reutilizables, como `Campo` (campo de texto con validación) y `SelectorDeLista` |
| `ui/theme/` | Colores de la fundación, tipografía y tema Material 3 |

## Arquitectura

La app sigue **MVVM**: cada capa conoce solo a la que tiene debajo.

```
Pantalla (Compose) ─→ ViewModel ─→ Repositorio ─→ Room (DAO)
```

| Capa | Responsabilidad | Ejemplo en el código |
|---|---|---|
| **Pantalla** | Solo dibuja el estado y avisa cuando el usuario toca algo. No decide nada | `RegistroScreen` |
| **Estado (`UiState`)** | Un `data class` con todo lo que la pantalla necesita mostrar, incluidos los errores de cada campo | `RegistroUiState` |
| **ViewModel** | Guarda el estado, contiene la lógica y sobrevive a la rotación de la pantalla | `RegistroViewModel` |
| **Repositorio** | Decide de dónde salen los datos. El ViewModel no sabe si vienen de Room o del servidor | `SesionRepository` |
| **ServiceLocator** | Crea la base de datos y los repositorios una sola vez y los entrega a los ViewModels mediante una *factory* | `ServiceLocator` |

Reglas del código:

- El `MutableStateFlow` es privado en el ViewModel; la pantalla recibe una versión de solo lectura (`StateFlow`) y la observa con `collectAsStateWithLifecycle`.
- Las validaciones son funciones puras (`ValidacionesRegistro.kt`, `ValidacionesPerfil.kt`, …), fuera de la pantalla, para reutilizarlas en varios formularios y probarlas sin dibujar nada.
- Las funciones que acceden a datos son `suspend` y se lanzan con `viewModelScope`.

## Pruebas

Las pruebas unitarias (validaciones, ViewModels y repositorios) se ejecutan en el computador, sin emulador:

```bash
./gradlew testDebugUnitTest
```

Las pruebas de pantalla y de la base de datos necesitan un emulador o un celular conectado:

```bash
./gradlew connectedDebugAndroidTest
```

En Windows se usa `gradlew.bat` en vez de `./gradlew`, siempre desde la carpeta `android`. Los ViewModels se prueban con repositorios y DAO falsos escritos a mano (`app/src/test/.../data/repository/Falsos.kt`), sin librerías de *mocks*.

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

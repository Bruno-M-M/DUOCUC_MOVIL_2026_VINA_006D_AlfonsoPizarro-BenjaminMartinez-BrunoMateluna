# Microservicios · Fundación Textil Circular

Microservicios en Spring Boot que usa la app Android. Siguen el contrato de `docs/API.md` y guardan los datos en Supabase (`docs/supabase/`).

| Servicio | Carpeta | Puerto | Tarjeta |
|---|---|---|---|
| Usuarios y sesiones | `usuarios/` | 8081 | 9.4 |
| Productos | `productos/` | 8082 | 9.5 |
| Emprendedoras | `emprendedoras/` | 8083 | 9.6 |

## Requisitos

- JDK 21 o superior (en IntelliJ: *File → Project Structure → SDK*).
- No hace falta instalar Maven: cada servicio trae `mvnw`.

## Variables de entorno

Las credenciales nunca se escriben en el código ni en `application.properties`. Cada servicio las lee de variables de entorno (en `.env.example` está el formato):

| Variable | Qué es |
|---|---|
| `DB_URL` | URL JDBC del *Session pooler* de Supabase (*Connect → Type: JDBC → Method: Session pooler*), sin el usuario ni la clave |
| `DB_USUARIO` | Usuario del pooler, con la forma `postgres.<referencia del proyecto>` |
| `DB_CLAVE` | Contraseña de la base de datos (cada integrante la guarda en su gestor de contraseñas) |

En IntelliJ se configuran en *Run → Edit Configurations → Environment variables*. Esa configuración queda en `.idea/`, que no se sube al repositorio.

## Cómo ejecutar

En IntelliJ: abrir la carpeta del servicio (por ejemplo `backend/usuarios`) y ejecutar la clase `*Application`.

Desde la terminal, dentro de la carpeta del servicio y con las variables de entorno definidas:

```
./mvnw spring-boot:run
```

Para comprobar que está arriba: `GET http://localhost:8081/salud` responde `{"estado":"ok","servicio":"usuarios"}`.

Desde el emulador de Android, el computador es `10.0.2.2` (por ejemplo `http://10.0.2.2:8081/`).

## Pruebas

```
./mvnw test
```

Las pruebas no necesitan la base de datos.

## Estructura de cada servicio

```
com.fundaciontextil.<servicio>
├── controller   rutas REST (@RestController)
├── service      reglas del negocio
├── repository   acceso a las tablas (Spring Data JPA)
├── model        entidades JPA y DTO
└── error        códigos de error de docs/API.md y su respuesta { "codigo", "mensaje" }
```

- Para responder un error de la API se lanza `new ApiException(CodigoError.CORREO_YA_REGISTRADO)`; `ManejadorErrores` lo convierte en el estado HTTP y el JSON del contrato.
- Los datos inválidos (`@Valid`) responden `400 DATOS_INVALIDOS`, una ruta inexistente `404 NO_ENCONTRADO` y cualquier error no controlado `500 ERROR_INTERNO`, sin mostrar detalles internos.
- `spring.jpa.hibernate.ddl-auto=validate`: Hibernate no crea ni cambia tablas. Las tablas se crean con los scripts de `docs/supabase/`.

## Crear un servicio nuevo a partir de `usuarios`

1. Copiar la carpeta `usuarios` con el nombre del servicio (por ejemplo `productos`), sin `target/` ni `.idea/`.
2. En `pom.xml`, cambiar `artifactId`, `name` y `description`.
3. Renombrar el paquete `com.fundaciontextil.usuarios` (en IntelliJ: clic derecho → *Refactor → Rename*) y la clase `UsuariosApplication`.
4. En `application.properties`, cambiar `spring.application.name` y `server.port`.
5. En `SaludController` y su prueba, cambiar el nombre del servicio.
6. Ejecutar `./mvnw test`.

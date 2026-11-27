# Contrato de la API · Fundación Textil Circular

Este documento es el contrato entre la app Android y el servidor. Lo escribe Bruno (tarjeta 5.1) y los tres integrantes lo acuerdan antes de la semana 2. Si una ruta o un campo cambia, se actualiza aquí primero.

## Convenciones

- **Base URL:** se configura en `BuildConfig.BASE_URL` (en el emulador apunta al servidor local; el servidor falso de la 5.2 responde las mismas rutas).
- **Formato:** JSON, codificación UTF-8, fechas en ISO 8601 (`2026-11-21T10:30:00-03:00`).
- **Identificadores:** números enteros (`Long`), generados por el servidor.
- **Precios:** enteros en pesos chilenos, sin decimales (`"precio": 4500`).
- **Campos opcionales:** se devuelven como `null` cuando no tienen valor.
- **Errores:** cualquier respuesta de error sigue este formato:

```json
{
  "codigo": "CORREO_YA_REGISTRADO",
  "mensaje": "Ya existe una cuenta con ese correo."
}
```

## Autenticación

- Se usa **JWT** firmado con HS256. La clave secreta se lee de la variable de entorno
  `JWT_SECRETO`, la misma en los tres microservicios. Nunca va en el repositorio.
- El token lo entregan `POST /sesiones` y `POST /usuarios` (registro), así la app entra
  directo después de registrarse.
- Contenido del token: `sub` (id del usuario), `rol` y `exp` (vence a las 24 horas).
- La app lo envía en la cabecera `Authorization: Bearer <token>` en las rutas marcadas con 🔒.
- Sin token, con un token inválido o vencido: `401 NO_AUTENTICADO`.
- Con un token válido pero sobre datos de otra persona: `403 PROHIBIDO`.

## Modelos

### Usuario

| Campo | Tipo | Notas |
|---|---|---|
| `id` | entero | |
| `nombre` | texto | |
| `correo` | texto | único |
| `rol` | texto | `CLIENTE`, `EMPRENDEDORA` o `ADMINISTRADORA` |
| `estado` | texto | `PENDIENTE`, `VALIDADA`, `RECHAZADA`, `PAUSADA`, `SUSPENDIDA` o `DADA_DE_BAJA` |
| `telefono` | texto o `null` | |
| `region` | texto o `null` | |
| `comuna` | texto o `null` | |

```json
{
  "id": 12,
  "nombre": "María Soto",
  "correo": "maria@ejemplo.cl",
  "rol": "EMPRENDEDORA",
  "estado": "VALIDADA",
  "telefono": "+56912345678",
  "region": "Valparaíso",
  "comuna": "Viña del Mar"
}
```

La contraseña **nunca** viaja en las respuestas. Solo viaja en las peticiones de registro e inicio de sesión, y el servidor la guarda con un hash.

### Emprendedora

| Campo | Tipo | Notas |
|---|---|---|
| `id` | entero | |
| `nombre` | texto | nombre de la persona |
| `emprendimiento` | texto | nombre comercial |
| `region` | texto o `null` | |
| `comuna` | texto | |
| `categorias` | lista de texto | 1 o 2 categorías de servicio, de la lista `CATEGORIAS_SERVICIO` de la app (`data/model/Territorio.kt`) |

```json
{
  "id": 3,
  "nombre": "María Soto",
  "emprendimiento": "Tejidos Soto",
  "region": "Valparaíso",
  "comuna": "Viña del Mar",
  "categorias": ["Confección con retazos", "Reparación y arreglos"]
}
```

En la base de datos, `categorias` va en una columna nueva `categorias text[]` de la tabla `emprendedoras`, que se agrega en la tarjeta 9.6.

### Producto

| Campo | Tipo | Notas |
|---|---|---|
| `id` | entero | |
| `emprendedoraId` | entero | id de la emprendedora dueña |
| `nombre` | texto | |
| `descripcion` | texto | |
| `precio` | entero | pesos chilenos |
| `fotoArchivo` | texto o `null` | nombre del archivo de la foto; la app lo pide a `/fotos/{archivo}` |

```json
{
  "id": 41,
  "emprendedoraId": 3,
  "nombre": "Chal de lana merino",
  "descripcion": "Tejido a mano, 100% lana merino.",
  "precio": 18990,
  "fotoArchivo": "chal-41.jpg"
}
```

## Usuarios

| Método | Ruta | Descripción | Token | Respuesta exitosa |
|---|---|---|---|---|
| `POST` | `/usuarios` | Registro de cliente o emprendedora | No | `201` con `usuario` y `token` |
| `POST` | `/sesiones` | Inicio de sesión | No | `200` con `usuario` y `token` |
| `GET` | `/usuarios/{id}` | Perfil de un usuario | 🔒 solo el mismo usuario | `200` con el `Usuario` |
| `PUT` | `/usuarios/{id}` | Editar perfil (nombre, teléfono, región, comuna) | 🔒 solo el mismo usuario | `200` con el `Usuario` actualizado |
| `POST` | `/usuarios/recuperar-clave` | Envía correo para recuperar la contraseña | No | `202` sin cuerpo |

**Registro (`POST /usuarios`)** — petición:

```json
{
  "nombre": "Camila Rojas",
  "correo": "camila@ejemplo.cl",
  "rut": "12.345.678-5",
  "clave": "clave-segura-123",
  "rol": "CLIENTE"
}
```

**Inicio de sesión (`POST /sesiones`)** — petición:

```json
{
  "correo": "camila@ejemplo.cl",
  "clave": "clave-segura-123"
}
```

**Registro e inicio de sesión** — respuesta (la app guarda el `token` para las rutas con 🔒):

```json
{
  "usuario": {
    "id": 12,
    "nombre": "Camila Rojas",
    "correo": "camila@ejemplo.cl",
    "rol": "CLIENTE",
    "estado": "VALIDADA",
    "telefono": null,
    "region": null,
    "comuna": null
  },
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Emprendedoras

| Método | Ruta | Descripción | Token | Respuesta exitosa |
|---|---|---|---|---|
| `GET` | `/emprendedoras` | Lista para Explorar | No (modo invitado) | `200` con lista de `Emprendedora` |
| `GET` | `/emprendedoras/{id}` | Ficha de una emprendedora | No (modo invitado) | `200` con la `Emprendedora` |
| `GET` | `/emprendedoras/{id}/productos` | Catálogo de la emprendedora | No (modo invitado) | `200` con lista de `Producto` |
| `POST` | `/emprendedoras` | Solicitud de emprendedora (envía su ficha) | 🔒 usuario con rol `EMPRENDEDORA` | `201` con la `Emprendedora` creada |

No hay catálogo general: los productos se ven dentro de la ficha de cada emprendedora (`GET /emprendedoras/{id}/productos`). El orden por cercanía lo calcula la app (tarjeta 4.3) con la `comuna` de cada emprendedora; la API no expone coordenadas.

`GET /emprendedoras` y `GET /emprendedoras/{id}` solo muestran emprendedoras cuya cuenta está `VALIDADA`. Una cuenta `PENDIENTE`, pausada, suspendida o dada de baja no aparece en Explorar (si se pide por id, responde `404 NO_ENCONTRADO`).

Los filtros de la 7.2 (región, comuna y servicio) se agregan como parámetros de consulta en `GET /emprendedoras`: `?region=`, `?comuna=`, `?servicio=`. Son opcionales. `?servicio=` devuelve las emprendedoras que tienen esa categoría en `categorias`.

**Solicitud de emprendedora (`POST /emprendedoras`)** — petición:

```json
{
  "emprendimiento": "Tejidos Soto",
  "region": "Valparaíso",
  "comuna": "Viña del Mar",
  "categorias": ["Confección con retazos", "Reparación y arreglos"]
}
```

- El servidor liga la ficha al usuario del token (`sub`); la petición no incluye `usuarioId`.
- `categorias` debe tener 1 o 2 valores de la lista de la app; si no, `400 DATOS_INVALIDOS`.
- La cuenta sigue `PENDIENTE` hasta que la administración la valide (tarjeta 10.6).
- Si el usuario ya tiene ficha: `409 YA_TIENE_FICHA`.

## Productos

| Método | Ruta | Descripción | Token | Respuesta exitosa |
|---|---|---|---|---|
| `GET` | `/productos/{id}` | Un producto | No (modo invitado) | `200` con el `Producto` |
| `POST` | `/productos` | Crear producto | 🔒 emprendedora `VALIDADA` | `201` con el `Producto` creado |
| `PUT` | `/productos/{id}` | Editar producto | 🔒 solo la dueña | `200` con el `Producto` actualizado |
| `DELETE` | `/productos/{id}` | Eliminar producto | 🔒 solo la dueña | `204` sin cuerpo |

En `POST` y `PUT` la petición no incluye `id`. El servidor lo asigna en `POST` y lo toma de la ruta en `PUT`.

En `POST /productos` el servidor busca la emprendedora del usuario **del token** (`sub`), no confía en el `emprendedoraId` del cuerpo. Así una emprendedora no puede crear productos a nombre de otra. Si el `emprendedoraId` enviado no es el suyo, responde `403 PROHIBIDO`.

```json
{
  "emprendedoraId": 3,
  "nombre": "Chal de lana merino",
  "descripcion": "Tejido a mano, 100% lana merino.",
  "precio": 18990,
  "fotoArchivo": null
}
```

## Fotos (para el EFT)

Estas rutas quedan definidas, pero **no se implementan en la Entrega 3**. Hasta entonces `fotoArchivo` viaja como `null` y las fotos solo se ven en el teléfono que las sacó (se guardan con `AlmacenFotos`).

| Método | Ruta | Descripción | Token | Respuesta exitosa |
|---|---|---|---|---|
| `POST` | `/fotos` | Subir una foto (`multipart/form-data`, campo `foto`, JPEG o PNG) | 🔒 | `201` con `{ "archivo": "chal-41.jpg" }` |
| `GET` | `/fotos/{archivo}` | Descargar una foto | No (modo invitado) | `200` con la imagen |

Para reemplazar la foto de un producto se sube la nueva con `POST /fotos` y se envía el nombre recibido en `fotoArchivo` con `PUT /productos/{id}`.

## Códigos de respuesta

| Código HTTP | Cuándo | Código de error (`codigo`) |
|---|---|---|
| `200`, `201`, `202`, `204` | Éxito | — |
| `400` | Datos inválidos (campo vacío, precio negativo, RUT mal formado) | `DATOS_INVALIDOS` |
| `401` | Correo o clave incorrectos | `CREDENCIALES_INCORRECTAS` |
| `401` | Ruta con 🔒 sin token, o con un token inválido o vencido | `NO_AUTENTICADO` |
| `403` | Cuenta dada de baja o suspendida | `CUENTA_DADA_DE_BAJA` |
| `403` | Token válido, pero sobre datos de otra persona (o sin el rol necesario) | `PROHIBIDO` |
| `404` | El recurso no existe | `NO_ENCONTRADO` |
| `409` | Correo o RUT ya registrados | `CORREO_YA_REGISTRADO`, `RUT_YA_REGISTRADO` |
| `409` | El usuario ya envió su ficha de emprendedora | `YA_TIENE_FICHA` |
| `500` | Error interno del servidor | `ERROR_INTERNO` |

La app traduce estos códigos al resultado sellado de la 5.2: `Exito`, `SinConexion` (cuando no hay red) o `ErrorServidor` (cualquier otro error).

## Pendientes para acordar entre los tres

- [ ] Confirmar las rutas de la sección *Usuarios* con Benjamín y Alfonso.
- [x] Decidir si `GET /productos` se filtra por emprendedora o solo existe la ruta anidada. **Decidido:** solo existe la ruta anidada `GET /emprendedoras/{id}/productos`; no hay catálogo general.
- [x] Confirmar el formato de la foto (`fotoArchivo`) para la 7.1 y la 6.2. **Resuelto:** ver la sección *Fotos* (se implementa para el EFT).

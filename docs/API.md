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
| `comuna` | texto | |

```json
{
  "id": 3,
  "nombre": "María Soto",
  "emprendimiento": "Tejidos Soto",
  "comuna": "Viña del Mar"
}
```

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

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| `POST` | `/usuarios` | Registro de cliente o emprendedora | `201` con el `Usuario` creado |
| `POST` | `/sesiones` | Inicio de sesión | `200` con el `Usuario` |
| `GET` | `/usuarios/{id}` | Perfil de un usuario | `200` con el `Usuario` |
| `PUT` | `/usuarios/{id}` | Editar perfil (nombre, teléfono, región, comuna) | `200` con el `Usuario` actualizado |
| `POST` | `/usuarios/recuperar-clave` | Envía correo para recuperar la contraseña | `202` sin cuerpo |

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

## Emprendedoras

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/emprendedoras` | Lista para Explorar | `200` con lista de `Emprendedora` |
| `GET` | `/emprendedoras/{id}` | Ficha de una emprendedora | `200` con la `Emprendedora` |
| `GET` | `/emprendedoras/{id}/productos` | Catálogo de la emprendedora | `200` con lista de `Producto` |

Los filtros de la 7.2 (región, comuna y servicio) se agregan como parámetros de consulta en `GET /emprendedoras`: `?region=`, `?comuna=`, `?servicio=`. Son opcionales.

## Productos

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| `GET` | `/productos` | Lista de todos los productos | `200` con lista de `Producto` |
| `GET` | `/productos/{id}` | Un producto | `200` con el `Producto` |
| `POST` | `/productos` | Crear producto (la emprendedora dueña) | `201` con el `Producto` creado |
| `PUT` | `/productos/{id}` | Editar producto | `200` con el `Producto` actualizado |
| `DELETE` | `/productos/{id}` | Eliminar producto | `204` sin cuerpo |

En `POST` y `PUT` la petición no incluye `id`. El servidor lo asigna en `POST` y lo toma de la ruta en `PUT`.

```json
{
  "emprendedoraId": 3,
  "nombre": "Chal de lana merino",
  "descripcion": "Tejido a mano, 100% lana merino.",
  "precio": 18990,
  "fotoArchivo": null
}
```

## Códigos de respuesta

| Código HTTP | Cuándo | Código de error (`codigo`) |
|---|---|---|
| `200`, `201`, `202`, `204` | Éxito | — |
| `400` | Datos inválidos (campo vacío, precio negativo, RUT mal formado) | `DATOS_INVALIDOS` |
| `401` | Correo o clave incorrectos | `CREDENCIALES_INCORRECTAS` |
| `403` | Cuenta dada de baja o suspendida | `CUENTA_DADA_DE_BAJA` |
| `404` | El recurso no existe | `NO_ENCONTRADO` |
| `409` | Correo o RUT ya registrados | `CORREO_YA_REGISTRADO`, `RUT_YA_REGISTRADO` |
| `500` | Error interno del servidor | `ERROR_INTERNO` |

La app traduce estos códigos al resultado sellado de la 5.2: `Exito`, `SinConexion` (cuando no hay red) o `ErrorServidor` (cualquier otro error).

## Pendientes para acordar entre los tres

- [ ] Confirmar las rutas de la sección *Usuarios* con Benjamín y Alfonso.
- [ ] Decidir si `GET /productos` se filtra por emprendedora o solo existe la ruta anidada.
- [ ] Confirmar el formato de la foto (`fotoArchivo`) para la 7.1 y la 6.2.

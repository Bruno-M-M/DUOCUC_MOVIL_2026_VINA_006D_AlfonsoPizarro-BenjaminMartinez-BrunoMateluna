# Base de datos en Supabase

Base de datos PostgreSQL en Supabase para los microservicios de Spring Boot (tarjetas 9.x).
La app no se conecta directo a Supabase: siempre pasa por los microservicios, siguiendo el contrato de `docs/API.md`.

## Tablas

| Tabla | Para qué | Relaciones |
|---|---|---|
| `usuarios` | Cuentas de clientes, emprendedoras y administradoras, con su rol y estado. | — |
| `emprendedoras` | Ficha pública de cada emprendedora: emprendimiento, región y comuna. Su nombre se toma de usuarios. | `usuario_id` → `usuarios` (una ficha por usuario) |
| `productos` | Catálogo de cada emprendedora: nombre, descripción, precio y foto. | `emprendedora_id` → `emprendedoras` (borrado en cascada) |

## Restricciones

- `correo` y `rut_hash` son únicos: una persona no puede tener dos cuentas.
- `rol` y `estado` solo aceptan los valores de `docs/API.md` (`CHECK`).
- Una emprendedora tiene una sola ficha (`usuario_id` único) y puede tener muchos productos.
- `precio` debe ser mayor que 0, y los nombres no pueden quedar vacíos.
- La clave se guarda como hash con sal, y el RUT como hash sin sal para que `rut_hash` único
  detecte cuentas repetidas. Así, si alguien obtiene la base de datos, no puede leerlos.

## Validaciones (tarjeta 9.2)

- **Formato del correo** (`validaciones.sql`): debe ser `nombre@dominio.ext`. Funciona junto
  con la regla de `esquema.sql` que lo obliga a ir en minúsculas y sin espacios.
- **RUT:** no se valida en la base de datos porque solo llega su hash. El formato y el
  dígito verificador los validan la app y el microservicio de usuarios (9.4).
- **RLS sin políticas:** RLS está activado en las tres tablas y no tiene políticas, así que
  nadie puede leer ni escribir por la API de Supabase. Como usamos nuestra propia tabla
  `usuarios` y no Supabase Auth, el control de que cada usuario edite solo lo suyo lo hace
  el microservicio.
- **Pruebas:** `prueba_validaciones.sql` tiene 8 pruebas con el error esperado de cada una.
  Se ejecutan una a una en el SQL Editor; las 6 primeras deben fallar.

## Cómo crear la base desde cero

1. En el SQL Editor de Supabase, ejecutar `esquema.sql`.
2. Ejecutar `validaciones.sql`.
3. Ejecutar `datos_ejemplo.sql` una sola vez (si se repite, duplica los productos).

Los datos de ejemplo son ficticios y sus cuentas no permiten iniciar sesión:
los hashes son de mentira. Las cuentas de prueba reales se crean con la API (tarjeta 9.4).

## Seguridad

- La Data API de Supabase está desactivada: las tablas no se pueden consultar desde internet con la clave pública.
- RLS está activado en todas las tablas como protección extra.
- La contraseña de la base de datos no va en el repositorio. Cada integrante la guarda en su
  gestor de contraseñas, y los microservicios la leen desde variables de entorno (tarjeta 9.3).
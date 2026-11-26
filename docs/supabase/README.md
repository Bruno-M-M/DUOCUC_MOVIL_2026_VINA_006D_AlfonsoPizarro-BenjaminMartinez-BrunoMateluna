# Base de datos en Supabase

(Una o dos frases: qué es, para qué la usan los microservicios y que la app no se conecta directo.)

## Tablas

| Tabla | Para qué | Relaciones |
|---|---|---|
| `usuarios` | ... | — |
| `emprendedoras` | ... | `usuario_id` → `usuarios` (una ficha por usuario) |
| `productos` | ... | `emprendedora_id` → `emprendedoras` (borrado en cascada) |

## Restricciones

(Lista corta: correo y RUT únicos, rol y estado con valores fijos,
precio mayor que 0, nombres no vacíos. Explica por qué el RUT y la clave van como hash.)

## Cómo crear la base desde cero

1. SQL Editor → ejecutar `esquema.sql`.
2. Ejecutar `datos_ejemplo.sql` una sola vez.

## Seguridad

(Que la Data API está desactivada, RLS activado, y que la contraseña
de la base de datos no va en el repositorio: se pasa por variables de entorno en la 9.3.)
--Correo sin formato valido
-- Prueba 1. Debe fallar (23514): check constraint "usuarios_correo_formato" (correo sin @)
insert into usuarios (nombre, correo, rut_hash, clave_hash, sal, rol, estado)
values ('Prueba', 'sin-arroba.cl', 'prueba-1', 'x', 'x', 'CLIENTE', 'VALIDADA');

--Correo con mayusculas
-- Prueba 2. Debe fallar (23514): check constraint "usuarios_correo_check" (correo con mayúsculas)
insert into usuarios (nombre, correo, rut_hash, clave_hash, sal, rol, estado)
values ('Prueba', 'Prueba@Ejemplo.cl', 'prueba-2', 'x', 'x', 'CLIENTE', 'VALIDADA');

--Correo repetido
-- Prueba 3. Debe fallar (23505): unique constraint "usuarios_correo_key" (correo ya registrado)
insert into usuarios (nombre, correo, rut_hash, clave_hash, sal, rol, estado)
values ('Prueba', 'maria@ejemplo.cl', 'prueba-3', 'x', 'x', 'CLIENTE', 'VALIDADA');

--Rol que no existe
-- Prueba 4. Debe fallar (23514): check constraint "usuarios_rol_check" (rol que no existe)
insert into usuarios (nombre, correo, rut_hash, clave_hash, sal, rol, estado)
values ('Prueba', 'prueba4@ejemplo.cl', 'prueba-4', 'x', 'x', 'ADMIN', 'VALIDADA');

--Nombre en blanco
-- Prueba 5. Debe fallar (23514): check constraint "usuarios_nombre_check" (nombre en blanco)
insert into usuarios (nombre, correo, rut_hash, clave_hash, sal, rol, estado)
values ('   ', 'prueba5@ejemplo.cl', 'prueba-5', 'x', 'x', 'CLIENTE', 'VALIDADA');

--Productos de una emprededora no existente
-- Prueba 6. Debe fallar (23503): foreign key constraint "productos_emprendedora_id_fkey" (emprendedora inexistente)
insert into productos (emprendedora_id, nombre, precio)
values (9999, 'Prueba', 1000);

--RLS activada en las tres tablas
-- Prueba 7. Debe responder 3 filas con rls_activada = true
select relname as tabla, relrowsecurity as rls_activada
from pg_class
where relname in ('usuarios', 'emprendedoras', 'productos');

--Sin politicas
-- Prueba 8. Debe responder "Success. No rows returned" (sin políticas: nadie entra por la API de Supabase)
select * from pg_policies where schemaname = 'public';
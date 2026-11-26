insert into usuarios (nombre, correo, rut_hash, clave_hash, sal, rol, estado, region, comuna) values
  ('María Soto',    'maria@ejemplo.cl',   'hash-ejemplo-1', 'hash-ejemplo-1', 'sal-1', 'EMPRENDEDORA', 'VALIDADA', 'Valparaíso', 'Viña del Mar'),
  ('Javiera Muñoz', 'javiera@ejemplo.cl', 'hash-ejemplo-2', 'hash-ejemplo-2', 'sal-2', 'EMPRENDEDORA', 'PENDIENTE', 'Valparaíso', 'Quilpué'),
  ('Camila Rojas',  'camila@ejemplo.cl',  'hash-ejemplo-3', 'hash-ejemplo-3', 'sal-3', 'CLIENTE',      'VALIDADA', null, null);

insert into emprendedoras (usuario_id, emprendimiento, region, comuna)
select id, 'Tejidos Soto', 'Valparaíso', 'Viña del Mar'
from usuarios where correo = 'maria@ejemplo.cl';
insert into emprendedoras (usuario_id, emprendimiento, region, comuna)
select id, 'Taller De Javi', 'Valparaíso', 'Quilpué'
from usuarios where correo = 'javiera@ejemplo.cl';

insert into productos (emprendedora_id, nombre, descripcion, precio, foto_archivo)
select id, 'Chal de lana merino', 'Tejido a mano, 100% lana merino.', 18990, null
from emprendedoras where emprendimiento = 'Tejidos Soto';

insert into productos (emprendedora_id, nombre, descripcion, precio, foto_archivo)
select id, 'Bufanda de alpaca', 'Tejido a telar.', 12500, null
from emprendedoras where emprendimiento = 'Tejidos Soto';

insert into productos (emprendedora_id, nombre, descripcion, precio, foto_archivo)
select id, 'Gorro de lana reciclada', 'Hecho con lana reciclada.', 6990, null
from emprendedoras where emprendimiento = 'Taller De Javi';
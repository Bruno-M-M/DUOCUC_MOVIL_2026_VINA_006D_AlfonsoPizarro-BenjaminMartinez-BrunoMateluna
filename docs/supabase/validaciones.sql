-- Validaciones extra de la tarjeta 9.2: formato del correo (el esquema base está en esquema.sql).
alter table usuarios
  add constraint usuarios_correo_formato
  check (correo ~ '^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$');
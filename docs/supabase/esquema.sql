create table usuarios (
  id          bigint generated always as identity primary key,
  nombre      text not null check (length(trim(nombre)) > 0),
  correo      text not null unique check (correo = lower(trim(correo))),
  rut_hash    text not null unique,
  clave_hash  text not null,
  sal         text not null,
  rol         text not null check (rol in ('CLIENTE', 'EMPRENDEDORA', 'ADMINISTRADORA')),
  estado      text not null check (estado in ('PENDIENTE', 'VALIDADA', 'RECHAZADA',
                                              'PAUSADA', 'SUSPENDIDA', 'DADA_DE_BAJA')),
  telefono    text,
  region      text,
  comuna      text,
  creado_en   timestamptz not null default now()
);

create table emprendedoras (
  id              bigint generated always as identity primary key,
  usuario_id      bigint not null unique references usuarios(id),
  emprendimiento  text not null check (length(trim(emprendimiento)) > 0),
  region          text,
  comuna          text,
  creado_en       timestamptz not null default now()
);

create table productos (
  id                bigint generated always as identity primary key,
  emprendedora_id   bigint not null references emprendedoras(id) on delete cascade,
  nombre            text not null check (length(trim(nombre)) > 0),
  descripcion       text,
  precio            integer not null check (precio > 0),
  foto_archivo      text,
  creado_en         timestamptz not null default now()
);
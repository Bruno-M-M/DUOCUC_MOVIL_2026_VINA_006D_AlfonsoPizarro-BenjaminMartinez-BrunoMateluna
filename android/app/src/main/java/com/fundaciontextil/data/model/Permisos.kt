package com.fundaciontextil.data.model

fun Rol.puedeGestionarFotos(): Boolean =
    this == Rol.EMPRENDEDORA || this == Rol.ADMINISTRADORA
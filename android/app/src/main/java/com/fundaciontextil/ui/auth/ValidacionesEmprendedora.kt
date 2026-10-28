package com.fundaciontextil.ui.auth

import com.fundaciontextil.data.model.MAXIMO_CATEGORIAS
import com.fundaciontextil.data.model.RegionesDeChile

fun validarNombreComercial(nombre: String): String? = when {
    nombre.isBlank() -> "Ingresa el nombre de tu emprendimiento"
    nombre.trim().length < 2 -> "El nombre del emprendimiento es muy corto"
    else -> null
}

fun validarRegion(region: String?): String? =
    if (region == null) "Elige tu región" else null

fun validarComuna(region: String?, comuna: String?): String? = when {
    comuna == null -> "Elige tu comuna"
    comuna !in RegionesDeChile.comunasDe(region) -> "Elige una comuna de tu región"
    else -> null
}

fun validarCategorias(elegidas: Set<String>): String? = when {
    elegidas.isEmpty() -> "Elige al menos una categoría"
    elegidas.size > MAXIMO_CATEGORIAS -> "Puedes elegir hasta $MAXIMO_CATEGORIAS categorías"
    else -> null
}

fun alternarCategoria(elegidas: Set<String>, categoria: String): Set<String>? = when {
    categoria in elegidas -> elegidas - categoria
    elegidas.size >= MAXIMO_CATEGORIAS -> null
    else -> elegidas + categoria
}
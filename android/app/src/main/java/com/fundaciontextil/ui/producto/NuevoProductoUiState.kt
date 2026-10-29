package com.fundaciontextil.ui.producto

import com.fundaciontextil.data.model.Emprendedora

data class NuevoProductoUiState(
    val cargando: Boolean = true,
    val puedePublicar: Boolean = false,
    val esAdmin: Boolean = false,
    val emprendedoras: List<Emprendedora> = emptyList(),
    val emprendedoraId: Long? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val intentoEnvio: Boolean = false,
    val guardando: Boolean = false,
    val publicado: Boolean = false,
    val mensaje: String? = null,
)
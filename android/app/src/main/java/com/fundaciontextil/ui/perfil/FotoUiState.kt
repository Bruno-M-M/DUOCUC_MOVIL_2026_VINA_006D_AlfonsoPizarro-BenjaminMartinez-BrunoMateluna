package com.fundaciontextil.ui.perfil

import java.io.File

data class FotoUiState(
    val cargando: Boolean = true,
    val puedeEditar: Boolean = false,
    val fotoGuardada: File? = null,
    val fotoPendiente: File? = null,
    val procesando: Boolean = false,
    val mensaje: String? = null,
)
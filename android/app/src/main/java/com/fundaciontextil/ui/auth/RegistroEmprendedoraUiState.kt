package com.fundaciontextil.ui.auth

import com.fundaciontextil.data.model.MAXIMO_CATEGORIAS
import com.fundaciontextil.data.model.RegionesDeChile

enum class CampoNegocio { NOMBRE_COMERCIAL, REGION, COMUNA, CATEGORIAS }

data class RegistroEmprendedoraUiState(
    val cuenta: RegistroUiState = RegistroUiState(),
    val nombreComercial: String = "",
    val region: String? = null,
    val comuna: String? = null,
    val categorias: Set<String> = emptySet(),
    val negocioTocado: Set<CampoNegocio> = emptySet(),
    val avisoMaximoCategorias: Boolean = false,
    val solicitudEnviada: Boolean = false,
) {
    val comunasDisponibles: List<String> get() = RegionesDeChile.comunasDe(region)

    val errorNombreComercial: String?
        get() = siTocado(CampoNegocio.NOMBRE_COMERCIAL) { validarNombreComercial(nombreComercial) }
    val errorRegion: String? get() = siTocado(CampoNegocio.REGION) { validarRegion(region) }
    val errorComuna: String? get() = siTocado(CampoNegocio.COMUNA) { validarComuna(region, comuna) }
    val errorCategorias: String? get() = siTocado(CampoNegocio.CATEGORIAS) { validarCategorias(categorias) }

    val puedeRegistrar: Boolean
        get() = cuenta.puedeRegistrar &&
                validarNombreComercial(nombreComercial) == null &&
                validarRegion(region) == null &&
                validarComuna(region, comuna) == null &&
                validarCategorias(categorias) == null

    private fun siTocado(campo: CampoNegocio, error: () -> String?): String? =
        if (campo in negocioTocado) error() else null

    companion object {
        const val MENSAJE_MAXIMO_CATEGORIAS =
            "Solo puedes elegir $MAXIMO_CATEGORIAS categorías. Quita una para elegir otra."
    }
}
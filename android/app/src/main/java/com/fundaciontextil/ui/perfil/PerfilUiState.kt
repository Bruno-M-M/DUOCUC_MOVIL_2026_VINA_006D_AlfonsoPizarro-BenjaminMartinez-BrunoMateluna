package com.fundaciontextil.ui.perfil

import com.fundaciontextil.data.model.RegionesDeChile
import com.fundaciontextil.data.model.Usuario
import com.fundaciontextil.ui.auth.validarComuna
import com.fundaciontextil.ui.auth.validarNombre

enum class CampoPerfil { NOMBRE, TELEFONO, COMUNA }

data class PerfilUiState(
    val cargando: Boolean = true,
    val guardado: Usuario? = null,
    val nombre: String = "",
    val telefono: String = "",
    val region: String? = null,
    val comuna: String? = null,
    val tocados: Set<CampoPerfil> = emptySet(),
    val guardando: Boolean = false,
    val cambiosGuardados: Boolean = false,
) {
    val errorNombre: String? get() = if (CampoPerfil.NOMBRE in tocados) validarNombre(nombre) else null
    val errorTelefono: String? get() = if (CampoPerfil.TELEFONO in tocados) validarTelefono(telefono) else null

    val errorComuna: String?
        get() = if (CampoPerfil.COMUNA in tocados && region != null) validarComuna(region, comuna) else null

    val comunasDisponibles: List<String> get() = RegionesDeChile.comunasDe(region)

    val hayCambios: Boolean
        get() = guardado != null && (
                nombre.trim() != guardado.nombre ||
                        telefono.trim() != guardado.telefono.orEmpty() ||
                        region != guardado.region ||
                        comuna != guardado.comuna
                )

    val puedeGuardar: Boolean
        get() = !guardando && hayCambios &&
                validarNombre(nombre) == null &&
                validarTelefono(telefono) == null &&
                (region == null || validarComuna(region, comuna) == null)


    companion object {
        const val MENSAJE_GUARDADO = "Cambios guardados"
    }
}

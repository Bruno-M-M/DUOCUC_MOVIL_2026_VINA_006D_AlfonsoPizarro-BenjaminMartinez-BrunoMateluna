package com.fundaciontextil.ui.explorar

import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.model.RegionesDeChile
import com.fundaciontextil.data.ubicacion.Coordenada
import com.fundaciontextil.data.ubicacion.distanciaAComuna

data class ExplorarUiState(
    val cargando: Boolean = true,
    val error: String? = null,
    val emprendedoras: List<Emprendedora> = emptyList(),
    val region: String? = null,
    val comuna: String? = null,
    val ubicacion: Coordenada? = null,
    val avisoUbicacion: String? = null,

    ) {
    val comunasDisponibles: List<String> get() = RegionesDeChile.comunasDe(region)

    val hayFiltros: Boolean get() = region != null || comuna != null

    val visibles: List<Emprendedora>
        get() = emprendedoras
            .filter { region == null || RegionesDeChile.regionDe(it.comuna) == region }
            .filter { comuna == null || it.comuna == comuna }
            .let { lista ->
                val desde = ubicacion ?: return@let lista
                lista.sortedBy { distanciaAComuna(desde, it.comuna) ?: Double.MAX_VALUE }
            }

}

package com.fundaciontextil.ui.explorar

import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.model.RegionesDeChile

data class ExplorarUiState(
    val cargando: Boolean = true,
    val error: String? = null,
    val emprendedoras: List<Emprendedora> = emptyList(),
    val region: String? = null,
    val comuna: String? = null,
) {
    val comunasDisponibles: List<String> get() = RegionesDeChile.comunasDe(region)

    val hayFiltros: Boolean get() = region != null || comuna != null

    val visibles: List<Emprendedora>
        get() = emprendedoras
            .filter { region == null || RegionesDeChile.regionDe(it.comuna) == region }
            .filter { comuna == null || it.comuna == comuna }
}

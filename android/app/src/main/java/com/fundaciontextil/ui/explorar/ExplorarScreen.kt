package com.fundaciontextil.ui.explorar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.model.RegionesDeChile
import com.fundaciontextil.data.ubicacion.UbicacionActual
import com.fundaciontextil.ui.components.SelectorDeLista
import com.fundaciontextil.ui.permisos.rememberPermisoUbicacion
import kotlinx.coroutines.launch

@Composable
fun ExplorarScreen(
    alVerEmprendedora: (Long) -> Unit,
    viewModel: ExplorarViewModel = viewModel(factory = ExplorarViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()
    val pedirUbicacion = rememberPermisoUbicacion {
        alcance.launch { viewModel.alObtenerUbicacion(UbicacionActual(contexto).obtener()) }
    }
    ExplorarContenido(
        uiState = uiState,
        alElegirRegion = { viewModel.alElegirRegion(it) },
        alElegirComuna = { viewModel.alElegirComuna(it) },
        alLimpiar = { viewModel.limpiarFiltros() },
        alReintentar = viewModel::cargar,
        alVerEmprendedora = alVerEmprendedora,
        alOrdenarPorCercania = pedirUbicacion,
    )
}

@Composable
internal fun ExplorarContenido(
    uiState: ExplorarUiState,
    alElegirRegion: (String) -> Unit,
    alElegirComuna: (String) -> Unit,
    alLimpiar: () -> Unit,
    alReintentar: () -> Unit,
    alVerEmprendedora: (Long) -> Unit,
    alOrdenarPorCercania: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Explorar", style = MaterialTheme.typography.headlineMedium)
        SelectorDeLista(
            etiqueta = "Región",
            seleccion = uiState.region,
            opciones = RegionesDeChile.regiones.map { it.nombre },
            alElegir = alElegirRegion,
            error = null,
        )
        SelectorDeLista(
            etiqueta = "Comuna",
            seleccion = uiState.comuna,
            opciones = uiState.comunasDisponibles,
            alElegir = alElegirComuna,
            error = null,
            habilitado = uiState.region != null,
        )
        TextButton(onClick = alOrdenarPorCercania) { Text("Ordenar por cercanía") }
        uiState.avisoUbicacion?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        if (uiState.hayFiltros) TextButton(onClick = alLimpiar) { Text("Limpiar filtros") }

        when {
            uiState.cargando -> CircularProgressIndicator()
            uiState.error != null -> {
                Text(uiState.error, color = MaterialTheme.colorScheme.error)
                Button(onClick = alReintentar) { Text("Reintentar") }
            }
            uiState.visibles.isEmpty() -> Text("No hay emprendedoras para mostrar.")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.visibles, key = { it.id }) { emprendedora ->
                    TarjetaEmprendedora(emprendedora) { alVerEmprendedora(emprendedora.id) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEmprendedora(emprendedora: Emprendedora, alTocar: () -> Unit) {
    Card(onClick = alTocar, modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Column(Modifier.padding(16.dp)) {
            Text(emprendedora.emprendimiento, style = MaterialTheme.typography.titleMedium)
            Text("${emprendedora.nombre} · ${emprendedora.comuna}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
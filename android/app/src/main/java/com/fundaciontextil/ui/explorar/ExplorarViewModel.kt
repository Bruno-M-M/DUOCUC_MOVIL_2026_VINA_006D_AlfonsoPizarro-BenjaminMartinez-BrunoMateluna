package com.fundaciontextil.ui.explorar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.repository.CatalogoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExplorarViewModel(
    private val repositorio: CatalogoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorarUiState())
    val uiState: StateFlow<ExplorarUiState> = _uiState.asStateFlow()

    private var trabajoCarga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        trabajoCarga?.cancel()
        _uiState.update { it.copy(cargando = true, error = null) }
        trabajoCarga = viewModelScope.launch {
            repositorio.emprendedoras
                .catch { _uiState.update { it.copy(cargando = false, error = MENSAJE_ERROR) } }
                .collect { lista -> _uiState.update { it.copy(cargando = false, emprendedoras = lista) } }
        }
    }

    // Al cambiar la región se borra la comuna, igual que en el perfil.
    fun alElegirRegion(region: String) = _uiState.update { it.copy(region = region, comuna = null) }

    fun alElegirComuna(comuna: String) = _uiState.update { it.copy(comuna = comuna) }

    fun limpiarFiltros() = _uiState.update { it.copy(region = null, comuna = null) }

    companion object {
        const val MENSAJE_ERROR = "No pudimos cargar las emprendedoras"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ExplorarViewModel(ServiceLocator.catalogoRepository) }
        }
    }
}

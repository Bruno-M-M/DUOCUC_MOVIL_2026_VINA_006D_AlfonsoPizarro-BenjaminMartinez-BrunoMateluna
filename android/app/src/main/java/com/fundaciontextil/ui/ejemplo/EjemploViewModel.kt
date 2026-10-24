package com.fundaciontextil.ui.ejemplo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.repository.PulsacionesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EjemploViewModel(
    private val repositorio: PulsacionesRepository,
) : ViewModel() {

    val uiState: StateFlow<EjemploUiState> = repositorio.pulsaciones
        .map { EjemploUiState(pulsaciones = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EjemploUiState(),
        )

    fun alPulsarSumar() {
        viewModelScope.launch { repositorio.sumar() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EjemploViewModel(ServiceLocator.pulsacionesRepository)
            }
        }
    }
}

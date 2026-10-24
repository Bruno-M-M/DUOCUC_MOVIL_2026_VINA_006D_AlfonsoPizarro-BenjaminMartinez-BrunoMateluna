package com.fundaciontextil.ui.ejemplo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EjemploViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(EjemploUiState())
    val uiState: StateFlow<EjemploUiState> = _uiState.asStateFlow()

    fun alPulsarSumar() {
        _uiState.update { it.copy(pulsaciones = it.pulsaciones + 1)}
    }
}
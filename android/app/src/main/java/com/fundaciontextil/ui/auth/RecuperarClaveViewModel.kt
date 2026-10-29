package com.fundaciontextil.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecuperarClaveViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RecuperarClaveUiState())
    val uiState: StateFlow<RecuperarClaveUiState> = _uiState.asStateFlow()

    fun alCambiarCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, correoTocado = true) }
    }

    fun alEnviar() {
        if (!_uiState.value.puedeEnviar) {
            _uiState.update { it.copy(correoTocado = true) }
            return
        }
        _uiState.update { it.copy(enviado = true) }
    }
}
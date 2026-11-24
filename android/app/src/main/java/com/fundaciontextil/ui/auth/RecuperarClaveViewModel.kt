package com.fundaciontextil.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fundaciontextil.data.remote.ResultadoRemoto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecuperarClaveViewModel(
    private val remoto: UsuarioRemoteRepository = UsuarioRemoteRepository(),
) : ViewModel() {

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
        viewModelScope.launch {
            // Solo se muestra la confirmación si el servidor recibió la solicitud.
            // Sin conexión no hay mensaje todavía; eso queda para la siguiente iteración de la pantalla.
            if (remoto.recuperarClave(_uiState.value.correo.trim()) is ResultadoRemoto.Exito) {
                _uiState.update { it.copy(enviado = true) }
            }
        }
    }
}

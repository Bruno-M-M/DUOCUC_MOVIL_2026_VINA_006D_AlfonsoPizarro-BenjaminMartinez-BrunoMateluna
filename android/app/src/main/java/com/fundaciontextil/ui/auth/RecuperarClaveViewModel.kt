package com.fundaciontextil.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.remote.ResultadoRemoto
import com.fundaciontextil.data.remote.UsuarioRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecuperarClaveViewModel(
    private val remoto: UsuarioRemoteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecuperarClaveUiState())
    val uiState: StateFlow<RecuperarClaveUiState> = _uiState.asStateFlow()

    fun alCambiarCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, correoTocado = true, errorEnvio = null) }
    }

    fun alEnviar() {
        val actual = _uiState.value
        if (!actual.puedeEnviar) {
            _uiState.update { it.copy(correoTocado = true) }
            return
        }
        _uiState.update { it.copy(enviando = true, errorEnvio = null) }
        viewModelScope.launch {
            val resultado = remoto.recuperarClave(actual.correo.trim())
            _uiState.update {
                when (resultado) {
                    is ResultadoRemoto.Exito -> it.copy(enviando = false, enviado = true)
                    ResultadoRemoto.SinConexion ->
                        it.copy(enviando = false, errorEnvio = RecuperarClaveUiState.MENSAJE_SIN_CONEXION)
                    is ResultadoRemoto.ErrorServidor ->
                        it.copy(enviando = false, errorEnvio = RecuperarClaveUiState.MENSAJE_SERVIDOR)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RecuperarClaveViewModel(ServiceLocator.usuarioRemoteRepository) }
        }
    }
}

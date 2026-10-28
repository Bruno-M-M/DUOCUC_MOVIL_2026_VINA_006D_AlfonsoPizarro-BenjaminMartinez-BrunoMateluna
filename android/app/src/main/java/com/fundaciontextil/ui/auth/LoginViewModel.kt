package com.fundaciontextil.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.repository.SesionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sesion: SesionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun alCambiarCorreo(correo: String) {
        _uiState.update { it.copy(correo = correo, correoTocado = true, errorCredenciales = null) }
    }

    fun alCambiarClave(clave: String) {
        _uiState.update { it.copy(clave = clave, claveTocada = true, errorCredenciales = null) }
    }

    fun alIngresar(alExito: () -> Unit) {
        val actual = _uiState.value
        if (!actual.puedeIngresar) {
            _uiState.update { it.copy(correoTocado = true, claveTocada = true) }
            return
        }

        _uiState.update { it.copy(cargando = true, errorCredenciales = null) }
        viewModelScope.launch {
            when (sesion.iniciarSesion(actual.correo, actual.clave)) {
                is ResultadoInicioSesion.Exito -> {
                    _uiState.update { it.copy(cargando = false) }
                    alExito()
                }

                ResultadoInicioSesion.CredencialesIncorrectas ->
                    _uiState.update { it.copy(cargando = false, errorCredenciales = MENSAJE_CREDENCIALES) }

                ResultadoInicioSesion.CuentaDadaDeBaja ->
                    _uiState.update { it.copy(cargando = false, errorCredenciales = MENSAJE_BAJA) }
            }
        }
    }

    companion object {
        const val MENSAJE_CREDENCIALES = "Correo o contraseña incorrectos"
        const val MENSAJE_BAJA = "Esta cuenta fue dada de baja"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginViewModel(ServiceLocator.sesionRepository)
            }
        }
    }

}
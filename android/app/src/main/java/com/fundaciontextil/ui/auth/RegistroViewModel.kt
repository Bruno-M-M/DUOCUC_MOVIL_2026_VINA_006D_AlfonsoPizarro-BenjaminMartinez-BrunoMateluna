package com.fundaciontextil.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.repository.SesionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val sesion: SesionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun alCambiarNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, tocados = it.tocados + CampoRegistro.NOMBRE) }
    }

    // Al cambiar el correo o el RUT se borra el aviso de «ya registrado»: hay que volver a intentarlo.
    fun alCambiarCorreo(correo: String) {
        _uiState.update {
            it.copy(correo = correo, tocados = it.tocados + CampoRegistro.CORREO, correoYaRegistrado = false)
        }
    }

    fun alCambiarRut(rut: String) {
        _uiState.update {
            it.copy(rut = rut, tocados = it.tocados + CampoRegistro.RUT, rutYaRegistrado = false)
        }
    }

    fun alCambiarClave(clave: String) {
        _uiState.update { it.copy(clave = clave, tocados = it.tocados + CampoRegistro.CLAVE) }
    }

    fun alCambiarRepetirClave(repetida: String) {
        _uiState.update {
            it.copy(repetirClave = repetida, tocados = it.tocados + CampoRegistro.REPETIR_CLAVE)
        }
    }

    fun alCambiarTerminos(acepta: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = acepta, terminosTocados = true) }
    }

    fun alRegistrar(alExito: () -> Unit) {
        val actual = _uiState.value
        if (!actual.puedeRegistrar) {
            // Si se intenta registrar con el formulario incompleto, se muestran todos los errores.
            _uiState.update { it.copy(tocados = CampoRegistro.entries.toSet(), terminosTocados = true) }
            return
        }

        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            val resultado = sesion.registrar(actual.nombre, actual.correo, actual.rut, actual.clave, Rol.CLIENTE)
            when (resultado) {
                is ResultadoRegistro.Exito -> {
                    _uiState.update { it.copy(cargando = false) }
                    alExito()
                }
                ResultadoRegistro.CorreoYaRegistrado ->
                    _uiState.update { it.copy(cargando = false, correoYaRegistrado = true) }
                ResultadoRegistro.RutYaRegistrado ->
                    _uiState.update { it.copy(cargando = false, rutYaRegistrado = true) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistroViewModel(ServiceLocator.sesionRepository)
            }
        }
    }
}
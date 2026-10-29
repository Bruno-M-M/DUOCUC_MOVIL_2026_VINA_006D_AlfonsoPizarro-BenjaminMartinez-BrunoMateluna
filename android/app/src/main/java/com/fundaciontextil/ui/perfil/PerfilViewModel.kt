package com.fundaciontextil.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.repository.SesionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val sesion: SesionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    // El formulario parte con lo que está guardado en Room.
    init {
        viewModelScope.launch {
            val usuario = sesion.usuarioActual()
            _uiState.update {
                PerfilUiState(
                    cargando = false,
                    guardado = usuario,
                    nombre = usuario?.nombre.orEmpty(),
                    telefono = usuario?.telefono.orEmpty(),
                    region = usuario?.region,
                    comuna = usuario?.comuna,
                )
            }
        }
    }

    fun alCambiarNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, tocados = it.tocados + CampoPerfil.NOMBRE, cambiosGuardados = false) }
    }

    fun alCambiarTelefono(telefono: String) {
        _uiState.update {
            it.copy(telefono = telefono, tocados = it.tocados + CampoPerfil.TELEFONO, cambiosGuardados = false)
        }
    }

    // Al cambiar la región se borra la comuna y se pide elegir una nueva.
    fun alElegirRegion(region: String) {
        _uiState.update {
            it.copy(region = region, comuna = null, tocados = it.tocados + CampoPerfil.COMUNA, cambiosGuardados = false)
        }
    }

    fun alElegirComuna(comuna: String) {
        _uiState.update { it.copy(comuna = comuna, tocados = it.tocados + CampoPerfil.COMUNA, cambiosGuardados = false) }
    }

    fun alGuardar() {
        val actual = _uiState.value
        val usuario = actual.guardado ?: return
        if (!actual.puedeGuardar) {
            _uiState.update { it.copy(tocados = CampoPerfil.entries.toSet()) }
            return
        }

        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            val editado = usuario.copy(
                nombre = actual.nombre.trim(),
                telefono = actual.telefono.trim().ifEmpty { null },
                region = actual.region,
                comuna = actual.comuna,
            )
            sesion.actualizarPerfil(editado)
            _uiState.update {
                it.copy(
                    guardado = editado,
                    nombre = editado.nombre,
                    telefono = editado.telefono.orEmpty(),
                    guardando = false,
                    cambiosGuardados = true,
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PerfilViewModel(ServiceLocator.sesionRepository)
            }
        }
    }
}

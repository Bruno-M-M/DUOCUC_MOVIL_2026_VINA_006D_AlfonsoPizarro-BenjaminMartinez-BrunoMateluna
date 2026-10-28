package com.fundaciontextil.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.Emprendimiento
import com.fundaciontextil.data.model.ResultadoRegistro
import com.fundaciontextil.data.repository.RegistroEmprendedoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroEmprendedoraViewModel(
    private val repositorio: RegistroEmprendedoraRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroEmprendedoraUiState())
    val uiState: StateFlow<RegistroEmprendedoraUiState> = _uiState.asStateFlow()


    private fun actualizarCuenta(cambio: (RegistroUiState) -> RegistroUiState) {
        _uiState.update { it.copy(cuenta = cambio(it.cuenta)) }
    }

    fun alCambiarNombre(nombre: String) = actualizarCuenta {
        it.copy(nombre = nombre, tocados = it.tocados + CampoRegistro.NOMBRE)
    }

    fun alCambiarCorreo(correo: String) = actualizarCuenta {
        it.copy(correo = correo, tocados = it.tocados + CampoRegistro.CORREO, correoYaRegistrado = false)
    }

    fun alCambiarRut(rut: String) = actualizarCuenta {
        it.copy(rut = rut, tocados = it.tocados + CampoRegistro.RUT, rutYaRegistrado = false)
    }

    fun alCambiarClave(clave: String) = actualizarCuenta {
        it.copy(clave = clave, tocados = it.tocados + CampoRegistro.CLAVE)
    }

    fun alCambiarRepetirClave(repetida: String) = actualizarCuenta {
        it.copy(repetirClave = repetida, tocados = it.tocados + CampoRegistro.REPETIR_CLAVE)
    }

    fun alCambiarTerminos(acepta: Boolean) = actualizarCuenta {
        it.copy(aceptaTerminos = acepta, terminosTocados = true)
    }


    fun alCambiarNombreComercial(nombre: String) {
        _uiState.update {
            it.copy(nombreComercial = nombre, negocioTocado = it.negocioTocado + CampoNegocio.NOMBRE_COMERCIAL)
        }
    }

    fun alElegirRegion(region: String) {
        _uiState.update {
            it.copy(region = region, comuna = null, negocioTocado = it.negocioTocado + CampoNegocio.REGION)
        }
    }

    fun alElegirComuna(comuna: String) {
        _uiState.update { it.copy(comuna = comuna, negocioTocado = it.negocioTocado + CampoNegocio.COMUNA) }
    }

    fun alTocarCategoria(categoria: String) {
        _uiState.update {
            val nuevas = alternarCategoria(it.categorias, categoria)
            val tocado = it.negocioTocado + CampoNegocio.CATEGORIAS
            if (nuevas == null) {
                it.copy(avisoMaximoCategorias = true, negocioTocado = tocado)
            } else {
                it.copy(categorias = nuevas, avisoMaximoCategorias = false, negocioTocado = tocado)
            }
        }
    }


    fun alRegistrar() {
        val actual = _uiState.value
        if (!actual.puedeRegistrar) {
            _uiState.update {
                it.copy(
                    cuenta = it.cuenta.copy(tocados = CampoRegistro.entries.toSet(), terminosTocados = true),
                    negocioTocado = CampoNegocio.entries.toSet(),
                )
            }
            return
        }

        actualizarCuenta { it.copy(cargando = true) }
        viewModelScope.launch {
            val cuenta = actual.cuenta
            val emprendimiento = Emprendimiento(
                nombreComercial = actual.nombreComercial,
                region = actual.region!!,
                comuna = actual.comuna!!,
                categorias = actual.categorias,
            )
            when (repositorio.registrar(cuenta.nombre, cuenta.correo, cuenta.rut, cuenta.clave, emprendimiento)) {
                is ResultadoRegistro.Exito -> _uiState.update {
                    it.copy(cuenta = it.cuenta.copy(cargando = false), solicitudEnviada = true)
                }
                ResultadoRegistro.CorreoYaRegistrado ->
                    actualizarCuenta { it.copy(cargando = false, correoYaRegistrado = true) }
                ResultadoRegistro.RutYaRegistrado ->
                    actualizarCuenta { it.copy(cargando = false, rutYaRegistrado = true) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RegistroEmprendedoraViewModel(ServiceLocator.registroEmprendedoraRepository)
            }
        }
    }
}
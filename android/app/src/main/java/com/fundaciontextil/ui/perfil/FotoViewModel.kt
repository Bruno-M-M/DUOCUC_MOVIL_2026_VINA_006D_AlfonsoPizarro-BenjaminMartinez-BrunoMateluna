package com.fundaciontextil.ui.perfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.foto.AlmacenFotos
import com.fundaciontextil.data.foto.DestinoCamara
import com.fundaciontextil.data.repository.FotoPerfilRepository
import com.fundaciontextil.data.repository.ResultadoFoto
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FotoViewModel(
    private val repositorio: FotoPerfilRepository,
    private val almacen: AlmacenFotos,
) : ViewModel() {

    private val _estado = MutableStateFlow(FotoUiState())
    val estado: StateFlow<FotoUiState> = _estado.asStateFlow()

    private var trabajoCarga: Job? = null

    fun cargar() {
        trabajoCarga?.cancel()
        descartarPendiente()
        trabajoCarga = viewModelScope.launch {
            val usuario = repositorio.usuarioConPermiso()
            if (usuario == null) {
                _estado.value = FotoUiState(cargando = false, puedeEditar = false)
                return@launch
            }
            _estado.value = FotoUiState(cargando = false, puedeEditar = true)
            repositorio.observarArchivo(usuario.id).collect { nombre ->
                _estado.update { it.copy(fotoGuardada = nombre?.let(almacen::archivo)) }
            }
        }
    }

    fun crearDestinoCamara(): DestinoCamara? =
        if (_estado.value.puedeEditar) almacen.crearDestinoCamara() else null

    fun alResultadoCamara(archivo: File, tomada: Boolean) {
        if (!tomada || !_estado.value.puedeEditar || !almacen.esArchivoDeCamara(archivo)) {
            viewModelScope.launch { almacen.borrarTemporal(archivo) }
            return
        }
        procesar(origenTemporal = archivo) { archivo.inputStream() }
    }

    fun alFallarCamara(archivo: File?) {
        viewModelScope.launch { almacen.borrarTemporal(archivo) }
        _estado.update { it.copy(mensaje = "No se pudo abrir la cámara en este dispositivo") }
    }

    fun alElegirDeGaleria(uri: Uri?) {
        if (uri == null || !_estado.value.puedeEditar) return
        procesar(origenTemporal = null) { almacen.abrirUri(uri) }
    }

    fun guardar() {
        val pendiente = _estado.value.fotoPendiente ?: return
        viewModelScope.launch {
            _estado.update { it.copy(procesando = true) }
            val mensaje = try {
                when (repositorio.guardar(pendiente)) {
                    ResultadoFoto.Guardada -> "Foto guardada"
                    ResultadoFoto.SinSesion -> "Inicia sesión para guardar la foto"
                    ResultadoFoto.NoPermitido -> "Tu cuenta no puede cambiar la foto"
                }
            } catch (e: Exception) {
                "No se pudo guardar la foto"
            }
            _estado.update { it.copy(procesando = false, fotoPendiente = null, mensaje = mensaje) }
            almacen.borrarTemporal(pendiente.takeIf { it.exists() })
        }
    }

    fun descartarPendiente() {
        val pendiente = _estado.value.fotoPendiente ?: return
        _estado.update { it.copy(fotoPendiente = null) }
        viewModelScope.launch { almacen.borrarTemporal(pendiente) }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }

    private fun procesar(origenTemporal: File?, abrir: () -> InputStream) {
        viewModelScope.launch {
            _estado.update { it.copy(procesando = true) }
            try {
                val nueva = almacen.procesarAPendiente(abrir)
                val reemplazada = _estado.value.fotoPendiente
                _estado.update { it.copy(fotoPendiente = nueva, procesando = false) }
                almacen.borrarTemporal(reemplazada)
            } catch (e: Exception) {
                _estado.update { it.copy(procesando = false, mensaje = "No se pudo procesar la imagen") }
            } finally {
                withContext(NonCancellable) { almacen.borrarTemporal(origenTemporal) }
            }
        }
    }

    override fun onCleared() {
        _estado.value.fotoPendiente?.delete()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { FotoViewModel(ServiceLocator.fotoPerfilRepository, ServiceLocator.almacenFotos) }
        }
    }
}
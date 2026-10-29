package com.fundaciontextil.ui.foto

import android.net.Uri
import com.fundaciontextil.data.foto.AlmacenFotos
import com.fundaciontextil.data.foto.DestinoCamara
import java.io.File
import java.io.InputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EstadoFoto(
    val pendiente: File? = null,
    val procesando: Boolean = false,
    val mensaje: String? = null,
)

class ControlFoto(
    private val almacen: AlmacenFotos,
    private val ambito: CoroutineScope,
) {
    private val _estado = MutableStateFlow(EstadoFoto())
    val estado: StateFlow<EstadoFoto> = _estado.asStateFlow()

    fun crearDestinoCamara(): DestinoCamara = almacen.crearDestinoCamara()

    fun alResultadoCamara(archivo: File, tomada: Boolean) {
        if (!tomada || !almacen.esArchivoDeCamara(archivo)) {
            ambito.launch { almacen.borrarTemporal(archivo) }
            return
        }
        procesar(origenTemporal = archivo) { archivo.inputStream() }
    }

    fun alFallarCamara(archivo: File?) {
        ambito.launch { almacen.borrarTemporal(archivo) }
        _estado.update { it.copy(mensaje = "No se pudo abrir la cámara en este dispositivo") }
    }

    fun alElegirDeGaleria(uri: Uri?) {
        if (uri == null) return
        procesar(origenTemporal = null) { almacen.abrirUri(uri) }
    }

    fun tomarPendiente(): File? {
        val pendiente = _estado.value.pendiente
        _estado.update { it.copy(pendiente = null) }
        return pendiente
    }

    fun descartar() {
        val pendiente = _estado.value.pendiente ?: return
        _estado.update { it.copy(pendiente = null) }
        ambito.launch { almacen.borrarTemporal(pendiente) }
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }

    fun limpiar() {
        _estado.value.pendiente?.delete()
    }

    private fun procesar(origenTemporal: File?, abrir: () -> InputStream) {
        ambito.launch {
            _estado.update { it.copy(procesando = true) }
            try {
                val nueva = almacen.procesarAPendiente(abrir)
                val anterior = _estado.value.pendiente
                _estado.update { it.copy(pendiente = nueva, procesando = false) }
                almacen.borrarTemporal(anterior) // si ya había otra, no queda huérfana
            } catch (e: Exception) {
                _estado.update { it.copy(procesando = false, mensaje = "No se pudo procesar la imagen") }
            } finally {
                withContext(NonCancellable) { almacen.borrarTemporal(origenTemporal) }
            }
        }
    }
}
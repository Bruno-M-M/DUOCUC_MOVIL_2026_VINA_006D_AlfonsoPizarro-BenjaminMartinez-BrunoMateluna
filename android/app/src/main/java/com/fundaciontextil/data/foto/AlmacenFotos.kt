package com.fundaciontextil.data.foto

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DestinoCamara(val archivo: File, val uri: Uri)


class AlmacenFotos(private val context: Context) {

    private val dirCamara = File(context.cacheDir, "camara")
    private val dirPendientes = File(context.cacheDir, "pendientes")
    private val dirFotos = File(context.filesDir, "fotos")

    fun archivo(nombre: String): File = File(dirFotos, nombre)

    fun crearDestinoCamara(): DestinoCamara {
        dirCamara.mkdirs()
        val archivo = File.createTempFile("cam_", ".jpg", dirCamara)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        return DestinoCamara(archivo, uri)
    }

    fun esArchivoDeCamara(archivo: File): Boolean = archivo.parentFile == dirCamara

    suspend fun procesarAPendiente(abrir: () -> InputStream): File =
        withContext(Dispatchers.IO) {
            dirPendientes.mkdirs()
            val destino = File.createTempFile("pend_", ".jpg", dirPendientes)
            ProcesadorImagen.procesar(abrir, destino)
            destino
        }

    fun abrirUri(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri) ?: throw IOException("No se pudo abrir la imagen")

    suspend fun confirmar(pendiente: File): String = withContext(Dispatchers.IO) {
        dirFotos.mkdirs()
        val nombre = "foto_${UUID.randomUUID()}.jpg"
        val destino = File(dirFotos, nombre)
        if (!pendiente.renameTo(destino)) {
            pendiente.copyTo(destino, overwrite = true)
            pendiente.delete()
        }
        nombre
    }

    suspend fun borrar(nombre: String?) {
        if (nombre == null) return
        withContext(Dispatchers.IO) { archivo(nombre).takeIf { it.parentFile == dirFotos }?.delete() }
    }

    suspend fun borrarTemporal(archivo: File?) {
        if (archivo == null) return
        withContext(Dispatchers.IO) { archivo.delete() }
    }

    suspend fun limpiarHuerfanos(nombresEnUso: Set<String>, edadMinimaMs: Long = 60 * 60 * 1000L) =
        withContext(Dispatchers.IO) {
            val limite = System.currentTimeMillis() - edadMinimaMs
            fun File.viejo() = isFile && lastModified() < limite
            dirCamara.listFiles()?.filter { it.viejo() }?.forEach { it.delete() }
            dirPendientes.listFiles()?.filter { it.viejo() }?.forEach { it.delete() }
            dirFotos.listFiles()?.filter { it.viejo() && it.name !in nombresEnUso }?.forEach { it.delete() }
        }
}
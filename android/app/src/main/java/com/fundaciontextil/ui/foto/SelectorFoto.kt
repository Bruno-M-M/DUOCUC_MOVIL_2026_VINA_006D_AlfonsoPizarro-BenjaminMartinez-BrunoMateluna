package com.fundaciontextil.ui.foto

import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fundaciontextil.data.ServiceLocator
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SelectorFoto(control: ControlFoto, modifier: Modifier = Modifier) {
    val estado by control.estado.collectAsStateWithLifecycle()

    // Sobrevive a que Android mate la app mientras se toma la foto.
    var rutaCamara by rememberSaveable { mutableStateOf<String?>(null) }

    val lanzarCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        rutaCamara?.let { control.alResultadoCamara(File(it), tomada) }
        rutaCamara = null
    }
    val lanzarGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        control.alElegirDeGaleria(uri)
    }

    val ocupado = estado.procesando

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
            val imagen = rememberImagenDeArchivo(estado.pendiente)
            when {
                ocupado -> CircularProgressIndicator(Modifier.size(32.dp))
                imagen != null -> Image(
                    bitmap = imagen,
                    contentDescription = "Foto del producto",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                else -> Text("Aún no hay foto", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                enabled = !ocupado,
                onClick = {
                    val destino = control.crearDestinoCamara()
                    rutaCamara = destino.archivo.absolutePath
                    try {
                        lanzarCamara.launch(destino.uri)
                    } catch (e: ActivityNotFoundException) { // emulador sin app de cámara
                        rutaCamara = null
                        control.alFallarCamara(destino.archivo)
                    }
                },
            ) { Text("Tomar foto") }

            OutlinedButton(
                enabled = !ocupado,
                onClick = { lanzarGaleria.launch("image/*") },
            ) { Text("Elegir de galería") }
        }

        if (estado.pendiente != null) {
            TextButton(enabled = !ocupado, onClick = control::descartar) { Text("Quitar foto") }
        }

        estado.mensaje?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium)
            LaunchedEffect(it) {
                delay(3_000)
                control.mensajeMostrado()
            }
        }
    }
}

@Composable
fun FotoProducto(nombreArchivo: String?, modifier: Modifier = Modifier) {
    val archivo = remember(nombreArchivo) { nombreArchivo?.let(ServiceLocator.almacenFotos::archivo) }
    val imagen = rememberImagenDeArchivo(archivo, muestreo = 2)
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen,
                contentDescription = "Foto del producto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text("Sin foto", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun rememberImagenDeArchivo(archivo: File?, muestreo: Int = 1): ImageBitmap? =
    produceState<ImageBitmap?>(initialValue = null, archivo?.path, muestreo) {
        value = archivo?.let {
            withContext(Dispatchers.IO) {
                val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
                BitmapFactory.decodeFile(it.path, opciones)?.asImageBitmap()
            }
        }
    }.value
package com.fundaciontextil.ui.perfil

import android.content.ActivityNotFoundException
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FotoPerfilSeccion(viewModel: FotoViewModel = viewModel(factory = FotoViewModel.Factory)) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }

    var rutaCamara by rememberSaveable { mutableStateOf<String?>(null) }

    val lanzarCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        rutaCamara?.let { viewModel.alResultadoCamara(File(it), tomada) }
        rutaCamara = null
    }
    val lanzarGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.alElegirDeGaleria(uri)
    }

    if (estado.cargando || !estado.puedeEditar) return

    val mostrada = estado.fotoPendiente ?: estado.fotoGuardada
    val ocupado = estado.procesando

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Foto", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier.fillMaxWidth().height(220.dp),
            contentAlignment = Alignment.Center,
        ) {
            val imagen = rememberImagen(mostrada)
            when {
                ocupado -> CircularProgressIndicator(Modifier.size(32.dp))
                imagen != null -> Image(
                    bitmap = imagen,
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Fit,
                )
                else -> Text("Aún no hay foto", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                enabled = !ocupado,
                onClick = {
                    val destino = viewModel.crearDestinoCamara() ?: return@OutlinedButton
                    rutaCamara = destino.archivo.absolutePath
                    try {
                        lanzarCamara.launch(destino.uri)
                    } catch (e: ActivityNotFoundException) { // emulador sin app de cámara
                        rutaCamara = null
                        viewModel.alFallarCamara(destino.archivo)
                    }
                },
            ) { Text("Tomar foto") }

            OutlinedButton(
                enabled = !ocupado,
                onClick = { lanzarGaleria.launch("image/*") },
            ) { Text("Elegir de galería") }
        }

        if (estado.fotoPendiente != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = !ocupado, onClick = viewModel::guardar) { Text("Guardar") }
                OutlinedButton(enabled = !ocupado, onClick = viewModel::descartarPendiente) { Text("Descartar") }
            }
        }

        estado.mensaje?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium)
            LaunchedEffect(it) { viewModel.mensajeMostrado() }
        }
    }
}

@Composable
private fun rememberImagen(archivo: File?): ImageBitmap? =
    produceState<ImageBitmap?>(initialValue = null, archivo?.path) {
        value = archivo?.let {
            withContext(Dispatchers.IO) { BitmapFactory.decodeFile(it.path)?.asImageBitmap() }
        }
    }.value
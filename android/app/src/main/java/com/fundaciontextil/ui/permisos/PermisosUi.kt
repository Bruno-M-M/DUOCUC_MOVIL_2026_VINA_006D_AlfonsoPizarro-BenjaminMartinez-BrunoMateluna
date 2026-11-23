package com.fundaciontextil.ui.permisos

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun rememberPermisoAccion(
    permiso: String,
    explicacion: String,
    mensajeDenegado: String,
    accion: () -> Unit,
): () -> Unit {
    val contexto = LocalContext.current
    val accionActual by rememberUpdatedState(accion)
    var mostrarExplicacion by rememberSaveable { mutableStateOf(false) }
    var mostrarAjustes by rememberSaveable { mutableStateOf(false) }

    val pedir = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            accionActual()
        } else {
            // Tras pedirlo, si Android ya no deja mostrar el diálogo, el bloqueo es permanente.
            val actividad = contexto.buscarActividad()
            val bloqueado = actividad != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(actividad, permiso)
            if (bloqueado) mostrarAjustes = true
            else Toast.makeText(contexto, mensajeDenegado, Toast.LENGTH_LONG).show()
        }
    }

    if (mostrarExplicacion) {
        AlertDialog(
            onDismissRequest = { mostrarExplicacion = false },
            title = { Text("Permiso necesario") },
            text = { Text(explicacion) },
            confirmButton = {
                TextButton(onClick = {
                    mostrarExplicacion = false
                    pedir.launch(permiso)
                }) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarExplicacion = false
                    Toast.makeText(contexto, mensajeDenegado, Toast.LENGTH_LONG).show()
                }) { Text("Ahora no") }
            },
        )
    }

    if (mostrarAjustes) {
        AlertDialog(
            onDismissRequest = { mostrarAjustes = false },
            title = { Text("Permiso bloqueado") },
            text = { Text("$mensajeDenegado Puedes activarlo manualmente en Ajustes.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarAjustes = false
                    contexto.abrirAjustesDeLaApp()
                }) { Text("Abrir Ajustes") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAjustes = false }) { Text("Cancelar") }
            },
        )
    }

    return remember(contexto, permiso) {
        {
            val concedido = ContextCompat.checkSelfPermission(contexto, permiso) ==
                    PackageManager.PERMISSION_GRANTED
            if (concedido) accionActual() else mostrarExplicacion = true
        }
    }
}

@Composable
fun rememberPermisoCamara(accion: () -> Unit): () -> Unit = rememberPermisoAccion(
    permiso = Manifest.permission.CAMERA,
    explicacion = "Usamos la cámara solo para que puedas tomar la foto de tu perfil o de tus productos.",
    mensajeDenegado = "Sin permiso de cámara no puedes tomar fotos, pero sí elegirlas de la galería.",
    accion = accion,
)

private fun Context.buscarActividad(): Activity? {
    var actual: Context = this
    while (actual is ContextWrapper) {
        if (actual is Activity) return actual
        actual = actual.baseContext
    }
    return null
}

private fun Context.abrirAjustesDeLaApp() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
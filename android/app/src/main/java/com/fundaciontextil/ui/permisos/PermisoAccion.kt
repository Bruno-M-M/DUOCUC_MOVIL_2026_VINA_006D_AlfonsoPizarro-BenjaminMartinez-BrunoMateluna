
package com.fundaciontextil.ui.permisos

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private tailrec fun Context.buscarActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.buscarActivity()
        else -> null
    }

@Composable
fun rememberPermisoAccion(
    permisos: List<String>,
    explicacion: String,
    textoBloqueado: String =
        "El permiso está bloqueado. Puedes activarlo en Ajustes.",
    alDenegar: () -> Unit = {},
    alConceder: () -> Unit,
): () -> Unit {

    val context = LocalContext.current

    var mostrarExplicacion by remember {
        mutableStateOf(false)
    }

    var mostrarAjustes by remember {
        mutableStateOf(false)
    }

    fun concedido(): Boolean =
        permisos.all { permiso ->
            ContextCompat.checkSelfPermission(
                context,
                permiso
            ) == PackageManager.PERMISSION_GRANTED
        }

    val lanzador = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->

        if (concedido()) {
            alConceder()
        } else {
            val activity = context.buscarActivity()

            val bloqueado = activity != null &&
                    permisos.any { permiso ->
                        ContextCompat.checkSelfPermission(
                            context,
                            permiso
                        ) != PackageManager.PERMISSION_GRANTED &&
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                    activity,
                                    permiso
                                )
                    }

            if (bloqueado) {
                mostrarAjustes = true
            } else {
                alDenegar()
            }
        }
    }

    if (mostrarExplicacion) {
        AlertDialog(
            onDismissRequest = {
                mostrarExplicacion = false
                alDenegar()
            },
            text = {
                Text(explicacion)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false

                        if (concedido()) {
                            alConceder()
                        } else {
                            lanzador.launch(
                                permisos.toTypedArray()
                            )
                        }
                    }
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false
                        alDenegar()
                    }
                ) {
                    Text("Ahora no")
                }
            }
        )
    }

    if (mostrarAjustes) {
        AlertDialog(
            onDismissRequest = {
                mostrarAjustes = false
                alDenegar()
            },
            text = {
                Text(textoBloqueado)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarAjustes = false

                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            ).apply {
                                data = Uri.fromParts(
                                    "package",
                                    context.packageName,
                                    null
                                )
                            }
                        )
                    }
                ) {
                    Text("Abrir Ajustes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarAjustes = false
                        alDenegar()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    return {
        if (concedido()) {
            alConceder()
        } else {
            mostrarExplicacion = true
        }
    }
}

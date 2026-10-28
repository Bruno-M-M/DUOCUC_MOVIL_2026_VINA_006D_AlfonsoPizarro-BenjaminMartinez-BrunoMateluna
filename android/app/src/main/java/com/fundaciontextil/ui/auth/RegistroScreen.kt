package com.fundaciontextil.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.ui.components.Campo
import com.fundaciontextil.ui.components.TipoCampo
import com.fundaciontextil.ui.theme.FundacionTextilTheme

@Composable
fun RegistroScreen(
    alRegistrarse: () -> Unit,
    alIrALogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel(factory = RegistroViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RegistroContenido(
        uiState = uiState,
        alCambiarNombre = viewModel::alCambiarNombre,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alCambiarRut = viewModel::alCambiarRut,
        alCambiarClave = viewModel::alCambiarClave,
        alCambiarRepetirClave = viewModel::alCambiarRepetirClave,
        alCambiarTerminos = viewModel::alCambiarTerminos,
        alRegistrar = { viewModel.alRegistrar(alRegistrarse) },
        alIrALogin = alIrALogin,
        modifier = modifier,
    )
}

@Composable
internal fun RegistroContenido(
    uiState: RegistroUiState,
    alCambiarNombre: (String) -> Unit,
    alCambiarCorreo: (String) -> Unit,
    alCambiarRut: (String) -> Unit,
    alCambiarClave: (String) -> Unit,
    alCambiarRepetirClave: (String) -> Unit,
    alCambiarTerminos: (Boolean) -> Unit,
    alRegistrar: () -> Unit,
    alIrALogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var verTerminos by rememberSaveable { mutableStateOf(false) }
    val habilitado = !uiState.cargando

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        Campo(
            valor = uiState.nombre,
            alCambiar = alCambiarNombre,
            etiqueta = "Nombre completo",
            tipo = TipoCampo.NOMBRE,
            error = uiState.errorNombre,
            esValido = CampoRegistro.NOMBRE in uiState.tocados && uiState.errorNombre == null,
            habilitado = habilitado,
        )
        Campo(
            valor = uiState.correo,
            alCambiar = alCambiarCorreo,
            etiqueta = "Correo electrónico",
            tipo = TipoCampo.CORREO,
            error = uiState.errorCorreo,
            esValido = CampoRegistro.CORREO in uiState.tocados && uiState.errorCorreo == null,
            habilitado = habilitado,
        )
        Campo(
            valor = uiState.rut,
            alCambiar = alCambiarRut,
            etiqueta = "RUT (ej: 12.345.678-5)",
            tipo = TipoCampo.RUT,
            error = uiState.errorRut,
            esValido = CampoRegistro.RUT in uiState.tocados && uiState.errorRut == null,
            habilitado = habilitado,
        )
        Campo(
            valor = uiState.clave,
            alCambiar = alCambiarClave,
            etiqueta = "Contraseña",
            tipo = TipoCampo.CLAVE,
            error = uiState.errorClave,
            habilitado = habilitado,
        )
        Campo(
            valor = uiState.repetirClave,
            alCambiar = alCambiarRepetirClave,
            etiqueta = "Repetir contraseña",
            tipo = TipoCampo.CLAVE,
            error = uiState.errorRepetirClave,
            habilitado = habilitado,
            accionIme = ImeAction.Done,
        )

        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = uiState.aceptaTerminos,
                    enabled = habilitado,
                    role = Role.Checkbox,
                    onValueChange = alCambiarTerminos,
                ),
        ) {
            Checkbox(checked = uiState.aceptaTerminos, onCheckedChange = null, enabled = habilitado)
            Spacer(Modifier.width(8.dp))
            Text("Acepto los términos y la política de privacidad")
        }
        TextButton(onClick = { verTerminos = true }) {
            Text("Leer términos y política de privacidad")
        }
        uiState.errorTerminos?.let { mensaje ->
            Text(mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = alRegistrar,
            enabled = uiState.puedeRegistrar,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.cargando) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta…")
                }
            } else {
                Text("Crear cuenta")
            }
        }
        TextButton(onClick = alIrALogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }

    if (verTerminos) {
        DialogoTerminos(
            alAceptar = {
                alCambiarTerminos(true)
                verTerminos = false
            },
            alCerrar = { verTerminos = false },
        )
    }
}

@Composable
private fun DialogoTerminos(alAceptar: () -> Unit, alCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Términos y privacidad") },
        text = {
            Text(TEXTO_TERMINOS, modifier = Modifier.verticalScroll(rememberScrollState()))
        },
        confirmButton = { TextButton(onClick = alAceptar) { Text("Acepto") } },
        dismissButton = { TextButton(onClick = alCerrar) { Text("Cerrar") } },
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun RegistroVacioPreview() {
    FundacionTextilTheme {
        RegistroContenido(RegistroUiState(), {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RegistroConErroresPreview() {
    FundacionTextilTheme {
        RegistroContenido(
            RegistroUiState(
                nombre = "Al",
                correo = "ana@demo.cl",
                rut = "12.345.678-9",
                clave = "abc",
                repetirClave = "abd",
                tocados = CampoRegistro.entries.toSet(),
                terminosTocados = true,
                correoYaRegistrado = true,
            ),
            {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}
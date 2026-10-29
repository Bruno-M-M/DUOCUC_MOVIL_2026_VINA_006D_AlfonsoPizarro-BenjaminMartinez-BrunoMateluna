package com.fundaciontextil.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.ui.components.Campo
import com.fundaciontextil.ui.components.TipoCampo
import com.fundaciontextil.ui.theme.FundacionTextilTheme

@Composable
fun RecuperarClaveScreen(
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecuperarClaveViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RecuperarClaveContenido(
        uiState = uiState,
        alCambiarCorreo = viewModel::alCambiarCorreo,
        alEnviar = viewModel::alEnviar,
        alVolver = alVolver,
        modifier = modifier,
    )
}

@Composable
internal fun RecuperarClaveContenido(
    uiState: RecuperarClaveUiState,
    alCambiarCorreo: (String) -> Unit,
    alEnviar: () -> Unit,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        AnimatedContent(
            targetState = uiState.enviado,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "recuperarClave",
        ) { enviado ->
            if (enviado) {
                Confirmacion()
            } else {
                Formulario(uiState, alCambiarCorreo, alEnviar)
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = alVolver, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Volver a iniciar sesión")
        }
    }
}

@Composable
private fun Formulario(
    uiState: RecuperarClaveUiState,
    alCambiarCorreo: (String) -> Unit,
    alEnviar: () -> Unit,
) {
    Column {
        Text(
            "Escribe el correo con el que creaste tu cuenta y te enviaremos los pasos para crear una nueva contraseña.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))
        Campo(
            valor = uiState.correo,
            alCambiar = alCambiarCorreo,
            etiqueta = "Correo electrónico",
            tipo = TipoCampo.CORREO,
            error = uiState.errorCorreo,
            esValido = uiState.correoTocado && uiState.errorCorreo == null,
            accionIme = ImeAction.Done,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = alEnviar,
            enabled = uiState.puedeEnviar,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Enviar instrucciones")
        }
    }
}

@Composable
private fun Confirmacion() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = RecuperarClaveUiState.MENSAJE_CONFIRMACION,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RecuperarClaveVacioPreview() {
    FundacionTextilTheme {
        RecuperarClaveContenido(RecuperarClaveUiState(), {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun RecuperarClaveConErrorPreview() {
    FundacionTextilTheme {
        RecuperarClaveContenido(RecuperarClaveUiState(correo = "camila@", correoTocado = true), {}, {}, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun RecuperarClaveEnviadoPreview() {
    FundacionTextilTheme {
        RecuperarClaveContenido(RecuperarClaveUiState(correo = "camila@demo.cl", enviado = true), {}, {}, {})
    }
}
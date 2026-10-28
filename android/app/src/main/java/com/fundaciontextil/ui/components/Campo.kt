package com.fundaciontextil.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

enum class TipoCampo(val icono: ImageVector) {
    TEXTO(Icons.Default.Create),
    NOMBRE(Icons.Default.Person),
    CORREO(Icons.Default.Email),
    CLAVE(Icons.Default.Lock),
    RUT(Icons.Default.AccountBox),
    TELEFONO(Icons.Default.Phone),
}

@Composable
fun Campo(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    tipo: TipoCampo = TipoCampo.TEXTO,
    error: String? = null,
    esValido: Boolean = false,
    marcarError: Boolean = false,
    habilitado: Boolean = true,
    accionIme: ImeAction = ImeAction.Next,
) {
    val esClave = tipo == TipoCampo.CLAVE
    var claveVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth(),
        enabled = habilitado,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(tipo.icono, contentDescription = null) },
        trailingIcon = {
            when {
                esClave -> TextButton(
                    onClick = { claveVisible = !claveVisible },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                ) { Text(if (claveVisible) "Ocultar" else "Mostrar") }

                esValido && error == null -> Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Campo válido",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        supportingText = error?.let { mensaje ->
            {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Campo con error",
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(mensaje)
                }
            }
        },
        isError = error != null || marcarError,
        singleLine = true,
        visualTransformation = if (esClave && !claveVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = when (tipo) {
                TipoCampo.CORREO -> KeyboardType.Email
                TipoCampo.CLAVE -> KeyboardType.Password
                TipoCampo.TELEFONO -> KeyboardType.Phone
                else -> KeyboardType.Text
            },
            capitalization = if (tipo == TipoCampo.NOMBRE) KeyboardCapitalization.Words else KeyboardCapitalization.None,
            imeAction = accionIme,
        ),
    )
}
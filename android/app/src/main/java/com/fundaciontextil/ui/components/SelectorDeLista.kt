package com.fundaciontextil.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorDeLista(
    etiqueta: String,
    seleccion: String?,
    opciones: List<String>,
    alElegir: (String) -> Unit,
    error: String?,
    habilitado: Boolean = true,
) {
    var abierto by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = abierto && habilitado,
        onExpandedChange = { if (habilitado) abierto = it },
    ) {
        OutlinedTextField(
            value = seleccion.orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = habilitado,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            isError = error != null,
            supportingText = error?.let { mensaje -> { Text(mensaje) } },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, habilitado),
        )
        ExposedDropdownMenu(expanded = abierto && habilitado, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        alElegir(opcion)
                        abierto = false
                    },
                )
            }
        }
    }
}

package com.fundaciontextil.ui.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.data.model.ValidacionesProducto
import com.fundaciontextil.ui.foto.SelectorFoto
import kotlinx.coroutines.delay

@Composable
fun NuevoProductoScreen(
    alPublicado: () -> Unit,
    alVolver: () -> Unit,
    viewModel: NuevoProductoViewModel = viewModel(factory = NuevoProductoViewModel.Factory),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.cargar() }
    LaunchedEffect(estado.publicado) { if (estado.publicado) alPublicado() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Agregar producto", style = MaterialTheme.typography.headlineSmall)

        when {
            estado.cargando -> Unit
            !estado.puedePublicar -> {
                Text("Solo emprendedoras y administradoras pueden publicar productos.")
                OutlinedButton(onClick = alVolver) { Text("Volver") }
            }
            else -> {
                val ver = estado.intentoEnvio

                if (estado.esAdmin) {
                    Text("Emprendedora", style = MaterialTheme.typography.titleMedium)
                    estado.emprendedoras.forEach { e ->
                        Row(
                            modifier = Modifier.fillMaxWidth().selectable(
                                selected = estado.emprendedoraId == e.id,
                                onClick = { viewModel.alElegirEmprendedora(e.id) },
                                role = Role.RadioButton,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(selected = estado.emprendedoraId == e.id, onClick = null)
                            Text("${e.nombre} · ${e.emprendimiento}", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    if (ver && estado.emprendedoraId == null) {
                        Text("Elige una emprendedora", color = MaterialTheme.colorScheme.error)
                    }
                }

                val errorNombre = ValidacionesProducto.nombre(estado.nombre)
                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = viewModel::alCambiarNombre,
                    label = { Text("Nombre del producto") },
                    singleLine = true,
                    isError = ver && errorNombre != null,
                    supportingText = { if (ver && errorNombre != null) Text(errorNombre) },
                    modifier = Modifier.fillMaxWidth(),
                )

                val errorDescripcion = ValidacionesProducto.descripcion(estado.descripcion)
                OutlinedTextField(
                    value = estado.descripcion,
                    onValueChange = viewModel::alCambiarDescripcion,
                    label = { Text("Descripción") },
                    minLines = 2,
                    isError = ver && errorDescripcion != null,
                    supportingText = { if (ver && errorDescripcion != null) Text(errorDescripcion) },
                    modifier = Modifier.fillMaxWidth(),
                )

                val errorPrecio = ValidacionesProducto.precio(estado.precio)
                OutlinedTextField(
                    value = estado.precio,
                    onValueChange = viewModel::alCambiarPrecio,
                    label = { Text("Precio (CLP)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = ver && errorPrecio != null,
                    supportingText = { if (ver && errorPrecio != null) Text(errorPrecio) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Foto (opcional)", style = MaterialTheme.typography.titleMedium)
                SelectorFoto(control = viewModel.foto)

                Button(
                    enabled = !estado.guardando,
                    onClick = viewModel::publicar,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(if (estado.guardando) "Publicando..." else "Publicar producto") }

                estado.mensaje?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    LaunchedEffect(it) {
                        delay(3_000)
                        viewModel.mensajeMostrado()
                    }
                }
            }
        }
    }
}
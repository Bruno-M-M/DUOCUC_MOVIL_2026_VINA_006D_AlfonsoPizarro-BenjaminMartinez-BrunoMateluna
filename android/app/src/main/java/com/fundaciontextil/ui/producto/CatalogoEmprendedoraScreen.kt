package com.fundaciontextil.ui.producto

import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.remote.ResultadoRemoto
import com.fundaciontextil.ui.foto.FotoProducto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CatalogoEmprendedoraScreen(
    emprendedoraId: Long,
    alVolver: () -> Unit,
    dolarViewModel: DolarViewModel = viewModel(),
) {
    val repositorio = ServiceLocator.catalogoRepository
    val productos by remember(emprendedoraId) { repositorio.productosDe(emprendedoraId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val emprendedoras by repositorio.emprendedoras.collectAsStateWithLifecycle(initialValue = emptyList())
    val resultadoDolar by dolarViewModel.dolar.collectAsStateWithLifecycle()
    val dolar = (resultadoDolar as? ResultadoRemoto.Exito)?.datos
    val emprendedora = emprendedoras.firstOrNull { it.id == emprendedoraId }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            emprendedora?.emprendimiento ?: "Catálogo",
            style = MaterialTheme.typography.headlineSmall,
        )
        emprendedora?.let { Text("${it.nombre} · ${it.comuna}", style = MaterialTheme.typography.bodyMedium) }

        Text(mensajeDolar(resultadoDolar), style = MaterialTheme.typography.bodySmall)

        if (productos.isEmpty()) Text("Esta emprendedora aún no tiene productos.")

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(productos, key = { it.id }) { producto ->
                var abierto by rememberSaveable(producto.id) { mutableStateOf(false) }
                Card(onClick = { abierto = !abierto }, modifier = Modifier.fillMaxWidth().animateContentSize()) {
                    Column {
                        FotoProducto(
                            nombreArchivo = producto.fotoArchivo,
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                        )
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                producto.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = if (abierto) Int.MAX_VALUE else 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(formatoPrecio(producto.precio), style = MaterialTheme.typography.titleSmall)
                            dolar?.let { Text(precioEnDolares(producto.precio, it), style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = alVolver) { Text("Volver") }
    }
}

private fun formatoPrecio(precio: Int): String =
    "$" + NumberFormat.getIntegerInstance(Locale("es", "CL")).format(precio)
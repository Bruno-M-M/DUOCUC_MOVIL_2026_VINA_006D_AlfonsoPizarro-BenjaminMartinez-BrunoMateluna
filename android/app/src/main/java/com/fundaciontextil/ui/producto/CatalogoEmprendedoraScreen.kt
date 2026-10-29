package com.fundaciontextil.ui.producto

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.ui.foto.FotoProducto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CatalogoEmprendedoraScreen(emprendedoraId: Long, alVolver: () -> Unit) {
    val repositorio = ServiceLocator.catalogoRepository
    val productos by remember(emprendedoraId) { repositorio.productosDe(emprendedoraId) }
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val emprendedoras by repositorio.emprendedoras.collectAsStateWithLifecycle(initialValue = emptyList())
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

        if (productos.isEmpty()) Text("Esta emprendedora aún no tiene productos.")

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(productos, key = { it.id }) { producto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        FotoProducto(
                            nombreArchivo = producto.fotoArchivo,
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                        )
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(producto.descripcion, style = MaterialTheme.typography.bodyMedium)
                            Text(formatoPrecio(producto.precio), style = MaterialTheme.typography.titleSmall)
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
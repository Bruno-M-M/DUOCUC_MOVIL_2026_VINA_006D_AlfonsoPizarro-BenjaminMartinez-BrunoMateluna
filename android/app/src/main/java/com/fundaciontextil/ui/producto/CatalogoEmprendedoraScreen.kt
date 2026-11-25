package com.fundaciontextil.ui.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.Producto
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

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Encabezado: ocupa todo el ancho de la grilla
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    emprendedora?.emprendimiento ?: "Catálogo",
                    style = MaterialTheme.typography.headlineSmall,
                )
                emprendedora?.let {
                    Text("${it.nombre} · ${it.comuna}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Mensaje de vacío (esto se mejora en el ítem 2)
        if (productos.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Esta emprendedora aún no tiene productos.")
            }
        }

        // Productos en grilla
        items(productos, key = { it.id }) { producto ->
            TarjetaProducto(producto)
        }

        // Botón volver: también ocupa todo el ancho
        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedButton(onClick = alVolver) { Text("Volver") }
        }
    }
}

@Composable
private fun TarjetaProducto(producto: Producto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            FotoProducto(
                nombreArchivo = producto.fotoArchivo,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            )
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    producto.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(formatoPrecio(producto.precio), style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

private fun formatoPrecio(precio: Int): String =
    "$" + NumberFormat.getIntegerInstance(Locale("es", "CL")).format(precio)
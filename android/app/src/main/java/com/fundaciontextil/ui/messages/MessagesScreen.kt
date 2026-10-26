package com.fundaciontextil.ui.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fundaciontextil.data.repository.ProductoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    productId: String
) {
    val producto = remember(productId) {
        if (productId != "general") ProductoRepository.obtenerPorId(productId) else null
    }

    var mensajeTexto by remember { mutableStateOf("") }
    var listaMensajes by remember {
        mutableStateOf(
            mutableListOf(
                "¡Hola! Bienvenido a Fundación Textil.",
                if (producto != null) "Consulta iniciada sobre el producto: ${producto.titulo}" else "Seleccione un producto del catálogo para consultar."
            )
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mensajería y Consultas") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (producto != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Producto en consulta: ${producto.titulo}", style = MaterialTheme.typography.titleMedium)
                        Text("Emprendedora: ${producto.emprendedora}", style = MaterialTheme.typography.bodySmall)
                        Text("Precio: \$${producto.precio}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaMensajes) { msg ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(text = msg, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = mensajeTexto,
                    onValueChange = { mensajeTexto = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (mensajeTexto.isNotBlank()) {
                            listaMensajes.add(mensajeTexto)
                            mensajeTexto = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}

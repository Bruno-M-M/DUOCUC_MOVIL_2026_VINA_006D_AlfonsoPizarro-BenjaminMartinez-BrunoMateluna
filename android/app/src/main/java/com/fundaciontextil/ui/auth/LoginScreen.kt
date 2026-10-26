package com.fundaciontextil.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (Rol) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateAsGuest: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val sesionRepository = ServiceLocator.sesionRepository

    Scaffold(
        topBar = { TopAppBar(title = { Text("Fundación Textil - Iniciar Sesión") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(mensajeError!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        when (val resultado = sesionRepository.iniciarSesion(correo, clave)) {
                            is ResultadoInicioSesion.Exito -> {
                                onLoginSuccess(resultado.usuario.rol)
                            }
                            ResultadoInicioSesion.CredencialesIncorrectas -> {
                                mensajeError = "Credenciales incorrectas"
                            }
                            ResultadoInicioSesion.CuentaDadaDeBaja -> {
                                mensajeError = "La cuenta ha sido dada de baja"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate")
            }

            OutlinedButton(
                onClick = onNavigateAsGuest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar como Invitado")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Accesos rápidos de prueba:", style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    correo = "camila@demo.cl"
                    clave = "Textil2026"
                }) { Text("Cliente") }
                Button(onClick = {
                    correo = "marcela@demo.cl"
                    clave = "Textil2026"
                }) { Text("Emprendedora") }
                Button(onClick = {
                    correo = "admin@demo.cl"
                    clave = "Textil2026"
                }) { Text("Admin") }
            }
        }
    }
}

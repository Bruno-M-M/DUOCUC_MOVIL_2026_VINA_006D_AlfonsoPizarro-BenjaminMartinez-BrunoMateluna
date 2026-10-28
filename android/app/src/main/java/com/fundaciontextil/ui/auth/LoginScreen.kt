package com.fundaciontextil.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.ResultadoInicioSesion
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (Rol) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateAsGuest: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var startAnimation by remember { mutableStateOf(false) }
    var showLoginContent by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sesionRepository = ServiceLocator.sesionRepository

    val scaleAnim = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        startAnimation = true
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(400) // Brief pause for splash effect
        showLoginContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Splash Logo with animation matching the official logo styling
            Column(
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .padding(vertical = if (showLoginContent) 16.dp else 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "TEXTIL",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "CIRCULAR",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "FUNDACIÓN",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Login Content appearing after splash animation
            AnimatedVisibility(
                visible = showLoginContent,
                enter = fadeIn(animationSpec = tween(700)) + slideInVertically(animationSpec = tween(700)) { 50 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        label = { Text("Correo electrónico", color = Color.LightGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = clave,
                        onValueChange = { clave = it },
                        label = { Text("Contraseña", color = Color.LightGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (mensajeError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(mensajeError!!, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

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
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar Sesión", fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = onNavigateToRegister) {
                        Text("¿No tienes cuenta? Regístrate", color = Color.LightGray)
                    }

                    OutlinedButton(
                        onClick = onNavigateAsGuest,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continuar como Invitado")
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Accesos rápidos de prueba:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = {
                            correo = "camila@demo.cl"
                            clave = "Textil2026"
                        }) { Text("Cliente", color = Color.White) }
                        TextButton(onClick = {
                            correo = "marcela@demo.cl"
                            clave = "Textil2026"
                        }) { Text("Emprendedora", color = Color.White) }
                        TextButton(onClick = {
                            correo = "admin@demo.cl"
                            clave = "Textil2026"
                        }) { Text("Admin", color = Color.White) }
                    }
                }
            }
        }
    }
}

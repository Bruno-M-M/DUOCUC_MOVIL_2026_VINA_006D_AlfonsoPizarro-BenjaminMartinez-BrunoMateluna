package com.fundaciontextil.ui.navigation

import com.fundaciontextil.ui.components.Accion
import com.fundaciontextil.ui.components.PantallaDemo
import com.fundaciontextil.ui.auth.LoginScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.EXPLORAR, "Explorar", Icons.Default.Search),
    Pestana(Rutas.MENSAJES, "Mensajes", Icons.Default.Email),
    Pestana(Rutas.NOVEDADES, "Novedades", Icons.Default.Notifications),
    Pestana(Rutas.PERFIL, "Perfil", Icons.Default.Person),
)

// Pantallas donde NO se muestra la barra inferior
private val sinBarra = setOf(Rutas.ACCESO, Rutas.LOGIN, Rutas.REGISTRO, Rutas.RECUPERAR_CLAVE)

// Qué pestaña se ve marcada según la pantalla en que estamos (un detalle pertenece a su pestaña)
private fun pestanaActiva(ruta: String?): String? = when {
    ruta == null -> null
    ruta.startsWith("emprendedora") || ruta.startsWith("producto") -> Rutas.EXPLORAR
    ruta.startsWith("chat") -> Rutas.MENSAJES
    else -> ruta
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    // Invitado = false, cliente con cuenta = true. Cuando tengan login real,
    // esto sale de un ViewModel (SessionViewModel) y no de una variable local.
    var haySesion by rememberSaveable { mutableStateOf(false) }

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarBarra = rutaActual != null && rutaActual !in sinBarra

    // Entrar a la app: se borra la pantalla de acceso para que "atrás" salga de la app
    fun irAExplorar() {
        navController.navigate(Rutas.EXPLORAR) {
            popUpTo(Rutas.ACCESO) { inclusive = true }
        }
    }

    // Se llama cuando el login o el registro salen bien
    fun terminarAcceso() {
        haySesion = true
        val vinoDelAcceso = runCatching { navController.getBackStackEntry(Rutas.ACCESO) }.isSuccess
        if (vinoDelAcceso) {
            irAExplorar()
        } else {
            // Venía desde dentro de la app (p. ej. del botón "comunicarse"): al cerrar
            // registro/login, la pantalla anterior queda a la vista, es decir, vuelve a la emprendedora.
            navController.popBackStack(Rutas.REGISTRO, inclusive = true)
            navController.popBackStack(Rutas.LOGIN, inclusive = true)
        }
    }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = pestanaActiva(rutaActual) == pestana.ruta,
                            onClick = {
                                navController.navigate(pestana.ruta) {
                                    // Al cambiar de pestaña no se apilan pantallas: se vuelve a la base
                                    // y se recuerda en qué punto estaba cada pestaña.
                                    popUpTo(Rutas.EXPLORAR) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(pestana.icono, contentDescription = pestana.titulo) },
                            label = { Text(pestana.titulo) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.ACCESO,
            modifier = Modifier.padding(padding),
        ) {
            // ---------- Acceso ----------
            composable(Rutas.ACCESO) {
                PantallaDemo(
                    "Bienvenida/o",
                    listOf(
                        Accion("Iniciar sesión") { navController.navigate(Rutas.LOGIN) },
                        Accion("Crear cuenta") { navController.navigate(Rutas.REGISTRO) },
                        Accion("Continuar como invitado") { irAExplorar() },
                    ),
                )
            }
            composable(Rutas.LOGIN) {
                LoginScreen(
                    alIniciarSesion = { terminarAcceso() },
                    alIrARegistro = { navController.navigate(Rutas.REGISTRO) },
                    alOlvidarClave = { navController.navigate(Rutas.RECUPERAR_CLAVE) }
                )
            }
            composable(Rutas.RECUPERAR_CLAVE) {
                PantallaDemo(
                    "Recuperar contraseña",
                    listOf(Accion("Volver") { navController.popBackStack() }),
                )
            }
            composable(Rutas.REGISTRO) {
                PantallaDemo(
                    "Crear cuenta de cliente",
                    listOf(
                        Accion("Registrarme (simula registro correcto)") { terminarAcceso() },
                        Accion("Volver") { navController.popBackStack() },
                    ),
                )
            }

            // ---------- Pestaña Explorar ----------
            composable(Rutas.EXPLORAR) {
                PantallaDemo(
                    "Explorar: lista de emprendedoras",
                    listOf(
                        Accion("Ver emprendedora 1") { navController.navigate(Rutas.emprendedora(1)) },
                        Accion("Ver emprendedora 2") { navController.navigate(Rutas.emprendedora(2)) },
                    ),
                )
            }
            composable(
                Rutas.EMPRENDEDORA,
                arguments = listOf(navArgument("emprendedoraId") { type = NavType.IntType }),
            ) { entrada ->
                val id = entrada.arguments?.getInt("emprendedoraId") ?: 0
                PantallaDemo(
                    "Catálogo de la emprendedora $id",
                    listOf(
                        Accion("Ver producto 10") { navController.navigate(Rutas.producto(10)) },
                        Accion("Volver") { navController.popBackStack() },
                    ),
                )
            }
            composable(
                Rutas.PRODUCTO,
                arguments = listOf(navArgument("productoId") { type = NavType.IntType }),
            ) { entrada ->
                val id = entrada.arguments?.getInt("productoId") ?: 0
                PantallaDemo(
                    "Detalle del producto $id",
                    listOf(
                        Accion("Comunicarme con la emprendedora") {
                            // Con sesión: abre el chat. Invitado: va al login, y al terminar vuelve aquí.
                            if (haySesion) navController.navigate(Rutas.chat(1))
                            else navController.navigate(Rutas.LOGIN)
                        },
                        Accion("Volver") { navController.popBackStack() },
                    ),
                )
            }

            // ---------- Pestaña Mensajes ----------
            composable(Rutas.MENSAJES) {
                if (haySesion) {
                    PantallaDemo(
                        "Mensajes",
                        listOf(Accion("Abrir chat 1") { navController.navigate(Rutas.chat(1)) }),
                    )
                } else {
                    PedirSesion(
                        "Inicia sesión para ver tus mensajes",
                        irALogin = { navController.navigate(Rutas.LOGIN) },
                        irARegistro = { navController.navigate(Rutas.REGISTRO) },
                    )
                }
            }
            composable(
                Rutas.CHAT,
                arguments = listOf(navArgument("chatId") { type = NavType.IntType }),
            ) { entrada ->
                val id = entrada.arguments?.getInt("chatId") ?: 0
                PantallaDemo("Conversación $id", listOf(Accion("Volver") { navController.popBackStack() }))
            }

            // ---------- Pestaña Novedades ----------
            composable(Rutas.NOVEDADES) { PantallaDemo("Novedades (lo ven todos, también el invitado)") }

            // ---------- Pestaña Perfil ----------
            composable(Rutas.PERFIL) {
                if (haySesion) {
                    PantallaDemo("Mi perfil", listOf(Accion("Cerrar sesión") { haySesion = false }))
                } else {
                    PedirSesion(
                        "Inicia sesión o crea tu cuenta",
                        irALogin = { navController.navigate(Rutas.LOGIN) },
                        irARegistro = { navController.navigate(Rutas.REGISTRO) },
                    )
                }
            }
        }
    }
}

// Misma ruta, distinto contenido: el invitado ve esto en Mensajes y Perfil
@Composable
private fun PedirSesion(mensaje: String, irALogin: () -> Unit, irARegistro: () -> Unit) {
    PantallaDemo(
        mensaje,
        listOf(Accion("Iniciar sesión", irALogin), Accion("Crear cuenta", irARegistro)),
    )
}

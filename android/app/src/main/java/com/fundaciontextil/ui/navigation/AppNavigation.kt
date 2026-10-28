package com.fundaciontextil.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.ui.auth.LoginScreen
import com.fundaciontextil.ui.auth.RegisterScreen
import com.fundaciontextil.ui.catalog.CatalogScreen
import com.fundaciontextil.ui.catalog.ProductDetailScreen
import com.fundaciontextil.ui.messages.MessagesScreen
import com.fundaciontextil.ui.profile.ProfileScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sesionRepository = ServiceLocator.sesionRepository
    var initialRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val usuario = sesionRepository.usuarioActual()
        initialRoute = when (usuario?.rol) {
            Rol.CLIENTE -> Screen.ClientCatalog.route
            Rol.EMPRENDEDORA -> Screen.EntrepreneurProducts.route
            Rol.ADMINISTRADORA -> Screen.AdminDashboard.route
            null -> Screen.Login.route
        }
    }

    if (initialRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isClientBottomBar = currentRoute in listOf(
        Screen.ClientCatalog.route,
        Screen.ClientMessages.createRoute(-1L),
        Screen.ClientProfile.route
    ) || currentRoute?.startsWith("client_messages/") == true

    val isEntrepreneurBottomBar = currentRoute in listOf(
        Screen.EntrepreneurProducts.route,
        Screen.EntrepreneurMessages.createRoute(-1L),
        Screen.EntrepreneurProfile.route
    ) || currentRoute?.startsWith("entrepreneur_messages/") == true

    Scaffold(
        bottomBar = {
            if (isClientBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Text("🛍️") },
                        label = { Text("Catálogo") },
                        selected = currentRoute == Screen.ClientCatalog.route,
                        onClick = {
                            navController.navigate(Screen.ClientCatalog.route) {
                                popUpTo(Screen.ClientCatalog.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("💬") },
                        label = { Text("Mensajes") },
                        selected = currentRoute?.startsWith("client_messages") == true,
                        onClick = {
                            navController.navigate(Screen.ClientMessages.createRoute(-1L)) {
                                popUpTo(Screen.ClientCatalog.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("👤") },
                        label = { Text("Perfil") },
                        selected = currentRoute == Screen.ClientProfile.route,
                        onClick = {
                            navController.navigate(Screen.ClientProfile.route) {
                                popUpTo(Screen.ClientCatalog.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            } else if (isEntrepreneurBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Text("📦") },
                        label = { Text("Productos") },
                        selected = currentRoute == Screen.EntrepreneurProducts.route,
                        onClick = {
                            navController.navigate(Screen.EntrepreneurProducts.route) {
                                popUpTo(Screen.EntrepreneurProducts.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("💬") },
                        label = { Text("Mensajes") },
                        selected = currentRoute?.startsWith("entrepreneur_messages") == true,
                        onClick = {
                            navController.navigate(Screen.EntrepreneurMessages.createRoute(-1L)) {
                                popUpTo(Screen.EntrepreneurProducts.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Text("👤") },
                        label = { Text("Perfil") },
                        selected = currentRoute == Screen.EntrepreneurProfile.route,
                        onClick = {
                            navController.navigate(Screen.EntrepreneurProfile.route) {
                                popUpTo(Screen.EntrepreneurProducts.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialRoute!!,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth / Splash-Login
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { rol ->
                        val destino = when (rol) {
                            Rol.CLIENTE -> Screen.ClientCatalog.route
                            Rol.EMPRENDEDORA -> Screen.EntrepreneurProducts.route
                            Rol.ADMINISTRADORA -> Screen.AdminDashboard.route
                        }
                        navController.navigate(destino) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateAsGuest = { navController.navigate(Screen.GuestCatalog.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { rol ->
                        val destino = when (rol) {
                            Rol.CLIENTE -> Screen.ClientCatalog.route
                            Rol.EMPRENDEDORA -> Screen.EntrepreneurProducts.route
                            Rol.ADMINISTRADORA -> Screen.AdminDashboard.route
                        }
                        navController.navigate(destino) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            // Invitado
            composable(Screen.GuestCatalog.route) {
                CatalogScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.GuestProductDetail.createRoute(productId))
                    },
                    title = "Catálogo (Modo Invitado)"
                )
            }
            composable(
                route = Screen.GuestProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
                ProductDetailScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onSendMessage = { _ ->
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            // Cliente
            composable(Screen.ClientCatalog.route) {
                CatalogScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.ClientProductDetail.createRoute(productId))
                    },
                    title = "Catálogo - Cliente"
                )
            }
            composable(
                route = Screen.ClientProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
                ProductDetailScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onSendMessage = { prodId ->
                        navController.navigate(Screen.ClientMessages.createRoute(prodId))
                    }
                )
            }
            composable(
                route = Screen.ClientMessages.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
                MessagesScreen(productId = productId)
            }
            composable(Screen.ClientProfile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Emprendedora
            composable(Screen.EntrepreneurProducts.route) {
                CatalogScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.EntrepreneurMessages.createRoute(productId))
                    },
                    title = "Mis Productos / Catálogo - Emprendedora"
                )
            }
            composable(
                route = Screen.EntrepreneurMessages.route,
                arguments = listOf(navArgument("productId") { type = NavType.LongType; defaultValue = -1L })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
                MessagesScreen(productId = productId)
            }
            composable(Screen.EntrepreneurProfile.route) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Administradora
            composable(Screen.AdminDashboard.route) {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Panel Administradora") }) }
                ) { padding ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Bienvenida Administradora", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                sesionRepository.cerrarSesion()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            }
        }
    }
}

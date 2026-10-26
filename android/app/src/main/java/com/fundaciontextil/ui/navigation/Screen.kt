package com.fundaciontextil.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    // Invitado
    object GuestCatalog : Screen("guest_catalog")
    object GuestProductDetail : Screen("guest_product_detail/{productId}") {
        fun createRoute(productId: String) = "guest_product_detail/$productId"
    }

    // Cliente (BottomBar)
    object ClientCatalog : Screen("client_catalog")
    object ClientMessages : Screen("client_messages/{productId}") {
        fun createRoute(productId: String = "general") = "client_messages/$productId"
    }
    object ClientProfile : Screen("client_profile")
    object ClientProductDetail : Screen("client_product_detail/{productId}") {
        fun createRoute(productId: String) = "client_product_detail/$productId"
    }

    // Emprendedora (BottomBar)
    object EntrepreneurProducts : Screen("entrepreneur_products")
    object EntrepreneurMessages : Screen("entrepreneur_messages/{productId}") {
        fun createRoute(productId: String = "general") = "entrepreneur_messages/$productId"
    }
    object EntrepreneurProfile : Screen("entrepreneur_profile")

    // Administradora
    object AdminDashboard : Screen("admin_dashboard")
    object AdminProfile : Screen("admin_profile")
}

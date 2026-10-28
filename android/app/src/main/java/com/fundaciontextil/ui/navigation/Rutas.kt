package com.fundaciontextil.ui.navigation

// Un solo lugar con todos los nombres de ruta, para no escribir textos sueltos por el código.
object Rutas {
    // Antes de entrar a la app (sin barra inferior)
    const val SPLASH = "splash"
    const val ACCESO = "acceso"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val REGISTRO_EMPRENDEDORA = "registro_emprendedora"
    const val RECUPERAR_CLAVE = "recuperar_clave"

    // Las 4 pestañas de la barra inferior
    const val EXPLORAR = "explorar"
    const val MENSAJES = "mensajes"
    const val NOVEDADES = "novedades"
    const val PERFIL = "perfil"

    // Pantallas de detalle: llevan un número (id) en la ruta
    const val EMPRENDEDORA = "emprendedora/{emprendedoraId}"
    const val PRODUCTO = "producto/{productoId}"
    const val CHAT = "chat/{chatId}"

    // Para navegar a un detalle se arma la ruta con el id real
    fun emprendedora(id: Int) = "emprendedora/$id"
    fun producto(id: Int) = "producto/$id"
    fun chat(id: Int) = "chat/$id"
}

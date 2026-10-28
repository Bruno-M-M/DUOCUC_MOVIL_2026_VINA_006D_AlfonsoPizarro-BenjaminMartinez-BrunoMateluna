package com.fundaciontextil.data.local

class DatosDeCatalogo(private val dao: CatalogoDao) {

    private class ProductoDemo(val nombre: String, val descripcion: String, val precio: Int)

    private class EmprendedoraDemo(
        val nombre: String,
        val emprendimiento: String,
        val comuna: String,
        val productos: List<ProductoDemo>,
    )

    private val catalogo = listOf(
        EmprendedoraDemo(
            "Marcela Soto", "Telar Verde", "Maipú",
            listOf(
                ProductoDemo("Chal teñido natural", "Lana reciclada teñida con tintes vegetales", 22000),
                ProductoDemo("Bufanda tejida a telar", "Hilo de algodón reciclado", 12000),
            ),
        ),
        EmprendedoraDemo(
            "Ana Rojas", "Retazos Ana", "Ñuñoa",
            listOf(
                ProductoDemo("Bolso patchwork", "Hecho con retazos de telas", 15000),
                ProductoDemo("Cojín reciclado", "Tela de jeans reutilizada", 8000),
            ),
        ),
        EmprendedoraDemo(
            "Sandra Muñoz", "Costura Circular", "La Florida",
            listOf(
                ProductoDemo("Delantal de lona", "Lona reutilizada, con bolsillos", 11000),
                ProductoDemo("Estuche de tela", "Cierre reciclado", 5000),
            ),
        ),
    )

    suspend fun cargarSiHaceFalta() {
        if (dao.contarEmprendedoras() > 0) return
        for (e in catalogo) {
            val id = dao.insertarEmprendedora(
                EmprendedoraEntity(nombre = e.nombre, emprendimiento = e.emprendimiento, comuna = e.comuna),
            )
            for (p in e.productos) {
                dao.insertarProducto(
                    ProductoEntity(emprendedoraId = id, nombre = p.nombre, descripcion = p.descripcion, precio = p.precio),
                )
            }
        }
    }
}
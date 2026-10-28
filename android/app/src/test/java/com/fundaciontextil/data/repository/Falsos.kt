package com.fundaciontextil.data.repository

import com.fundaciontextil.data.local.AlmacenSesion
import com.fundaciontextil.data.local.CatalogoDao
import com.fundaciontextil.data.local.EmprendedoraEntity
import com.fundaciontextil.data.local.ProductoEntity
import com.fundaciontextil.data.local.UsuarioDao
import com.fundaciontextil.data.local.UsuarioEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import com.fundaciontextil.data.local.SolicitudEmprendedoraDao
import com.fundaciontextil.data.local.SolicitudEmprendedoraEntity

internal class DaoFalso : UsuarioDao {
    val filas = mutableListOf<UsuarioEntity>()

    override suspend fun insertar(usuario: UsuarioEntity): Long {
        val id = filas.size + 1L
        filas += usuario.copy(id = id)
        return id
    }

    override suspend fun buscarPorCorreo(correo: String) = filas.firstOrNull { it.correo == correo }

    override suspend fun buscarPorRutHash(rutHash: String) = filas.firstOrNull { it.rutHash == rutHash }

    override suspend fun buscarPorId(id: Long) = filas.firstOrNull { it.id == id }
}

internal class AlmacenSesionEnMemoria : AlmacenSesion {
    private var id: Long? = null

    override fun usuarioId() = id

    override fun guardar(usuarioId: Long) {
        id = usuarioId
    }

    override fun limpiar() {
        id = null
    }
}

internal class CatalogoDaoFalso : CatalogoDao {
    val emprendedorasFlow = MutableStateFlow<List<EmprendedoraEntity>>(emptyList())
    val productosFlow = MutableStateFlow<List<ProductoEntity>>(emptyList())

    override fun observarEmprendedoras(): Flow<List<EmprendedoraEntity>> =
        emprendedorasFlow.map { lista -> lista.sortedBy { it.nombre } }

    override fun observarProductos(id: Long): Flow<List<ProductoEntity>> =
        productosFlow.map { lista -> lista.filter { it.emprendedoraId == id }.sortedBy { it.nombre } }

    override fun observarTodosLosProductos(): Flow<List<ProductoEntity>> =
        productosFlow.map { lista -> lista.sortedBy { it.nombre } }

    override suspend fun insertarEmprendedora(e: EmprendedoraEntity): Long {
        val id = emprendedorasFlow.value.size + 1L
        emprendedorasFlow.value = emprendedorasFlow.value + e.copy(id = id)
        return id
    }

    override suspend fun insertarProducto(p: ProductoEntity): Long {
        val id = productosFlow.value.size + 1L
        productosFlow.value = productosFlow.value + p.copy(id = id)
        return id
    }

    override suspend fun contarEmprendedoras(): Int = emprendedorasFlow.value.size
}

internal class SolicitudEmprendedoraDaoFalso : SolicitudEmprendedoraDao {
    val filas = mutableListOf<SolicitudEmprendedoraEntity>()

    override suspend fun insertar(solicitud: SolicitudEmprendedoraEntity) {
        filas += solicitud
    }

    override suspend fun buscarPorUsuario(usuarioId: Long) = filas.firstOrNull { it.usuarioId == usuarioId }
}

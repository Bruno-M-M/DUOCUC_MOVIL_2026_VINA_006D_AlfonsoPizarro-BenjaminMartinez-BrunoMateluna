package com.fundaciontextil.data.repository

import com.fundaciontextil.data.local.CatalogoDao
import com.fundaciontextil.data.local.EmprendedoraEntity
import com.fundaciontextil.data.local.ProductoEntity
import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CatalogoRepositoryLocal(private val dao: CatalogoDao) : CatalogoRepository {

    override val emprendedoras: Flow<List<Emprendedora>> =
        dao.observarEmprendedoras().map { lista -> lista.map { it.aModelo() } }

    override val productos: Flow<List<Producto>> =
        dao.observarTodosLosProductos().map { lista -> lista.map { it.aModelo() } }

    override fun productosDe(emprendedoraId: Long): Flow<List<Producto>> =
        dao.observarProductos(emprendedoraId).map { lista -> lista.map { it.aModelo() } }

    private fun EmprendedoraEntity.aModelo() = Emprendedora(
        id = id,
        nombre = nombre,
        emprendimiento = emprendimiento,
        comuna = comuna,
    )

    private fun ProductoEntity.aModelo() = Producto(
        id = id,
        emprendedoraId = emprendedoraId,
        nombre = nombre,
        descripcion = descripcion,
        precio = precio,
    )
}
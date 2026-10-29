package com.fundaciontextil.data.foto

import com.fundaciontextil.data.local.FotoDao
import com.fundaciontextil.data.local.PublicacionDao

class LimpiezaFotos(
    private val almacen: AlmacenFotos,
    private val fotoDao: FotoDao,
    private val publicacionDao: PublicacionDao,
) {
    suspend fun ejecutar() {
        val enUso = (fotoDao.todosLosArchivos() + publicacionDao.fotosDeProductosEnUso()).toSet()
        almacen.limpiarHuerfanos(enUso)
    }
}
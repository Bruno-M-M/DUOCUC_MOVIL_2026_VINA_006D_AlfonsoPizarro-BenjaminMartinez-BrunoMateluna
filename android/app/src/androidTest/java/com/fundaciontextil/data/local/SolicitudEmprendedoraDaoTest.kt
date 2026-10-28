package com.fundaciontextil.data.local

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SolicitudEmprendedoraDaoTest {

    private lateinit var baseDeDatos: AppDatabase
    private lateinit var usuarios: UsuarioDao
    private lateinit var dao: SolicitudEmprendedoraDao

    @Before
    fun crearBase() {
        baseDeDatos = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        usuarios = baseDeDatos.usuarioDao()
        dao = baseDeDatos.solicitudEmprendedoraDao()
    }

    @After
    fun cerrarBase() {
        baseDeDatos.close()
    }

    private fun solicitud(usuarioId: Long) = SolicitudEmprendedoraEntity(
        usuarioId = usuarioId,
        nombreComercial = "Telar Verde",
        region = "Valparaíso",
        comuna = "Viña del Mar",
        categoria1 = "Teñido natural",
        categoria2 = null,
    )

    private suspend fun crearMarta(): Long = usuarios.insertar(
        UsuarioEntity(
            nombre = "Marta",
            correo = "marta@demo.cl",
            rutHash = "hash-rut",
            claveHash = "hash-clave",
            sal = "sal",
            rol = Rol.EMPRENDEDORA,
            estado = EstadoCuenta.PENDIENTE,
        ),
    )

    @Test
    fun guarda_y_busca_la_solicitud_de_una_usuaria() = runBlocking {
        val id = crearMarta()

        dao.insertar(solicitud(id))

        assertEquals("Telar Verde", dao.buscarPorUsuario(id)?.nombreComercial)
        assertNull(dao.buscarPorUsuario(id + 1))
    }

    @Test
    fun no_acepta_una_solicitud_sin_cuenta() {
        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking { dao.insertar(solicitud(usuarioId = 99)) }
        }
    }
}
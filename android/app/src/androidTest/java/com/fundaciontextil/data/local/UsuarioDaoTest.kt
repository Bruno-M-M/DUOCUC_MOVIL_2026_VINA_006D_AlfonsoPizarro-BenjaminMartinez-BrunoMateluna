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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsuarioDaoTest {

    private lateinit var baseDeDatos: AppDatabase
    private lateinit var dao: UsuarioDao

    @Before
    fun crearBase() {
        baseDeDatos = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).build()
        dao = baseDeDatos.usuarioDao()
    }

    @After
    fun cerrarBase() {
        baseDeDatos.close()
    }

    private fun usuario(correo: String = "ana@demo.cl", rutHash: String = "hash-rut-1") =
        UsuarioEntity(
            nombre = "Ana",
            correo = correo,
            rutHash = rutHash,
            claveHash = "hash-clave",
            sal = "sal",
            rol = Rol.CLIENTE,
            estado = EstadoCuenta.VALIDADA,
        )

    @Test
    fun insertar_y_buscar_por_correo() = runBlocking {
        val id = dao.insertar(usuario())

        val encontrado = dao.buscarPorCorreo("ana@demo.cl")

        assertNotNull(encontrado)
        assertEquals(id, encontrado!!.id)
        assertEquals(Rol.CLIENTE, encontrado.rol)
    }

    @Test
    fun buscar_un_correo_que_no_existe_devuelve_null() = runBlocking {
        assertNull(dao.buscarPorCorreo("nadie@demo.cl"))
    }

    @Test
    fun actualizar_el_perfil_cambia_solo_los_datos_editables() = runBlocking {
        val id = dao.insertar(usuario())

        dao.actualizarPerfil(id, "Ana Pérez", "+56912345678", "Biobío", "Concepción")

        val actualizado = dao.buscarPorId(id)!!
        assertEquals("Ana Pérez", actualizado.nombre)
        assertEquals("+56912345678", actualizado.telefono)
        assertEquals("Concepción", actualizado.comuna)
        assertEquals("ana@demo.cl", actualizado.correo)
    }

    @Test
    fun no_permite_dos_usuarios_con_el_mismo_correo() {
        runBlocking { dao.insertar(usuario()) }

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking { dao.insertar(usuario(rutHash = "otro-rut")) }
        }
    }

    @Test
    fun no_permite_dos_usuarios_con_el_mismo_rut() {
        runBlocking { dao.insertar(usuario()) }

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking { dao.insertar(usuario(correo = "otra@demo.cl")) }
        }
    }
}

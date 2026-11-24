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

    @Test
    fun guardar_la_copia_dos_veces_actualiza_la_misma_cuenta() = runBlocking {
        dao.guardarCopia(usuario().copy(id = 7))
        dao.guardarCopia(usuario().copy(id = 7, nombre = "Ana Nueva", claveHash = "otra-clave"))

        assertEquals("Ana Nueva", dao.buscarPorId(7)!!.nombre)
        assertEquals("otra-clave", dao.buscarPorId(7)!!.claveHash)
    }

    @Test
    fun otra_cuenta_con_el_mismo_id_reemplaza_la_copia_y_libera_su_emprendimiento() = runBlocking {
        dao.guardarCopia(usuario().copy(id = 7))
        val emprendedoraId = baseDeDatos.publicacionDao().insertarEmprendedora(
            EmprendedoraEntity(nombre = "Ana", emprendimiento = "Telar", comuna = "Maipú", usuarioId = 7),
        )

        dao.guardarCopia(usuario(correo = "marta@demo.cl", rutHash = "hash-rut-2").copy(id = 7))

        assertNull(dao.buscarPorCorreo("ana@demo.cl"))
        assertEquals("marta@demo.cl", dao.buscarPorId(7)!!.correo)
        assertNull(baseDeDatos.publicacionDao().emprendedoraPorId(emprendedoraId)!!.usuarioId)
    }

    @Test
    fun si_el_servidor_cambia_el_id_queda_una_sola_copia() = runBlocking {
        dao.guardarCopia(usuario().copy(id = 7))

        dao.guardarCopia(usuario().copy(id = 20))

        assertNull(dao.buscarPorId(7))
        assertEquals("ana@demo.cl", dao.buscarPorId(20)!!.correo)
    }
}

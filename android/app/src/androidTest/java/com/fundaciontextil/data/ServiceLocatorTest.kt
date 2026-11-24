package com.fundaciontextil.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fundaciontextil.data.remote.ServidorFalso
import com.fundaciontextil.data.repository.UsuarioRepositorioConRespaldo
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

// Evita que la app vuelva a quedar conectada solo a Room sin que ninguna prueba lo note.
@RunWith(AndroidJUnit4::class)
class ServiceLocatorTest {

    @Test
    fun los_usuarios_pasan_por_la_api() {
        assertTrue(ServiceLocator.usuarioRepository is UsuarioRepositorioConRespaldo)
    }

    @Test
    fun en_depuracion_se_usa_el_servidor_falso() {
        assertTrue(ServiceLocator.apiService is ServidorFalso)
    }
}

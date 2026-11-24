package com.fundaciontextil.data

import android.content.Context
import com.fundaciontextil.data.foto.AlmacenFotos
import com.fundaciontextil.data.local.AlmacenSesionPreferencias
import com.fundaciontextil.data.local.AppDatabase
import com.fundaciontextil.data.local.CatalogoDao
import com.fundaciontextil.data.local.FotoDao
import com.fundaciontextil.data.local.SolicitudEmprendedoraDao
import com.fundaciontextil.data.local.UsuarioDao
import com.fundaciontextil.data.repository.CatalogoRepository
import com.fundaciontextil.data.repository.CatalogoRepositoryLocal
import com.fundaciontextil.data.repository.FotoPerfilRepository
import com.fundaciontextil.data.repository.RegistroEmprendedoraRepository
import com.fundaciontextil.data.repository.SesionRepository
import com.fundaciontextil.data.repository.UsuarioRepository
import com.fundaciontextil.data.repository.UsuarioRepositoryLocal
import com.fundaciontextil.data.foto.LimpiezaFotos
import com.fundaciontextil.data.local.PublicacionDao
import com.fundaciontextil.data.repository.ProductoPublicacionRepository
import com.fundaciontextil.BuildConfig
import com.fundaciontextil.data.remote.AlmacenCuentasPreferencias
import com.fundaciontextil.data.remote.ApiService
import com.fundaciontextil.data.remote.RetrofitInstance
import com.fundaciontextil.data.remote.ServidorFalso
import com.fundaciontextil.data.remote.UsuarioRemoteRepository
import com.fundaciontextil.data.repository.UsuarioRepositorioConRespaldo

object ServiceLocator {

    private lateinit var contexto: Context

    fun iniciar(context: Context) {
        contexto = context.applicationContext
    }

    private val baseDeDatos: AppDatabase by lazy { AppDatabase.crear(contexto) }

    val usuarioDao: UsuarioDao by lazy { baseDeDatos.usuarioDao() }

    val catalogoDao: CatalogoDao by lazy { baseDeDatos.catalogoDao() }

    val solicitudEmprendedoraDao: SolicitudEmprendedoraDao by lazy { baseDeDatos.solicitudEmprendedoraDao() }

    val fotoDao: FotoDao by lazy { baseDeDatos.fotoDao() }

    val catalogoRepository: CatalogoRepository by lazy { CatalogoRepositoryLocal(catalogoDao) }

    val sesionRepository: SesionRepository by lazy {
        SesionRepository(usuarioRepository, AlmacenSesionPreferencias(contexto))
    }

    val registroEmprendedoraRepository: RegistroEmprendedoraRepository by lazy {
        RegistroEmprendedoraRepository(sesionRepository, solicitudEmprendedoraDao)
    }

    val almacenFotos: AlmacenFotos by lazy { AlmacenFotos(contexto) }

    val fotoPerfilRepository: FotoPerfilRepository by lazy {
        FotoPerfilRepository(fotoDao, almacenFotos, sesionRepository)
    }

    val publicacionDao: PublicacionDao by lazy { baseDeDatos.publicacionDao() }

    val publicacionRepository: ProductoPublicacionRepository by lazy {
        ProductoPublicacionRepository(publicacionDao, solicitudEmprendedoraDao, almacenFotos, sesionRepository)
    }

    val limpiezaFotos: LimpiezaFotos by lazy { LimpiezaFotos(almacenFotos, fotoDao, publicacionDao) }

    // El único lugar donde se elige entre el servidor falso y el real.
    val apiService: ApiService by lazy {
        if (BuildConfig.USAR_SERVIDOR_FALSO) {
            ServidorFalso(almacen = AlmacenCuentasPreferencias(contexto))
        } else {
            RetrofitInstance.api
        }
    }

    val usuarioRemoteRepository: UsuarioRemoteRepository by lazy { UsuarioRemoteRepository(apiService) }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositorioConRespaldo(usuarioRemoteRepository, UsuarioRepositoryLocal(usuarioDao))
    }
}
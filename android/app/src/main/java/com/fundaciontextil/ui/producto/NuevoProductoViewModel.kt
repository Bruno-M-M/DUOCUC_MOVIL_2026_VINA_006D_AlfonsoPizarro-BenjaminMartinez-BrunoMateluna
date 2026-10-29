package com.fundaciontextil.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.foto.AlmacenFotos
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.model.ValidacionesProducto
import com.fundaciontextil.data.repository.NuevoProducto
import com.fundaciontextil.data.repository.ProductoPublicacionRepository
import com.fundaciontextil.data.repository.ResultadoPublicacion
import com.fundaciontextil.ui.foto.ControlFoto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NuevoProductoViewModel(
    private val repositorio: ProductoPublicacionRepository,
    almacen: AlmacenFotos,
) : ViewModel() {

    val foto = ControlFoto(almacen, viewModelScope)

    private val _estado = MutableStateFlow(NuevoProductoUiState())
    val estado: StateFlow<NuevoProductoUiState> = _estado.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            val usuario = repositorio.usuarioConPermiso()
            if (usuario == null) {
                _estado.update { it.copy(cargando = false, puedePublicar = false) }
                return@launch
            }
            val esAdmin = usuario.rol == Rol.ADMINISTRADORA
            val lista = if (esAdmin) repositorio.emprendedoras() else emptyList()
            _estado.update {
                it.copy(cargando = false, puedePublicar = true, esAdmin = esAdmin, emprendedoras = lista)
            }
        }
    }

    fun alCambiarNombre(v: String) = _estado.update { it.copy(nombre = v) }
    fun alCambiarDescripcion(v: String) = _estado.update { it.copy(descripcion = v) }
    fun alCambiarPrecio(v: String) = _estado.update { it.copy(precio = v.filter(Char::isDigit)) }
    fun alElegirEmprendedora(id: Long) = _estado.update { it.copy(emprendedoraId = id) }
    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }

    fun publicar() {
        val actual = _estado.value
        if (actual.guardando || !actual.puedePublicar) return
        _estado.update { it.copy(intentoEnvio = true) }

        val hayErrores = ValidacionesProducto.nombre(actual.nombre) != null ||
                ValidacionesProducto.descripcion(actual.descripcion) != null ||
                ValidacionesProducto.precio(actual.precio) != null ||
                (actual.esAdmin && actual.emprendedoraId == null)
        if (hayErrores) return

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true) }
            val datos = NuevoProducto(actual.nombre, actual.descripcion, actual.precio, actual.emprendedoraId)
            val resultado = try {
                repositorio.publicar(datos, foto.estado.value.pendiente)
            } catch (e: Exception) {
                null
            }
            when (resultado) {
                ResultadoPublicacion.Publicado -> {
                    foto.tomarPendiente()
                    _estado.update { it.copy(guardando = false, publicado = true) }
                }
                null -> {
                    foto.descartar()
                    _estado.update { it.copy(guardando = false, mensaje = "No se pudo publicar. Intenta de nuevo") }
                }
                else -> _estado.update {
                    it.copy(guardando = false, mensaje = "Tu cuenta no puede publicar este producto")
                }
            }
        }
    }

    override fun onCleared() {
        foto.limpiar()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { NuevoProductoViewModel(ServiceLocator.publicacionRepository, ServiceLocator.almacenFotos) }
        }
    }
}
package com.fundaciontextil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE rutHash = :rutHash LIMIT 1")
    suspend fun buscarPorRutHash(rutHash: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Query("UPDATE usuarios SET nombre = :nombre, telefono = :telefono, region = :region, comuna = :comuna WHERE id = :id")
    suspend fun actualizarPerfil(id: Long, nombre: String, telefono: String?, region: String?, comuna: String?)

    // Una copia es vieja si otra cuenta usa su id, o si su correo o su RUT ya tienen otro id en el servidor.
    @Query(
        "DELETE FROM usuarios WHERE (id = :id AND correo != :correo) " +
                "OR (id != :id AND (correo = :correo OR rutHash = :rutHash))"
    )
    suspend fun borrarCopiasViejas(id: Long, correo: String, rutHash: String): Int

    @Query("UPDATE emprendedoras SET usuarioId = NULL WHERE usuarioId NOT IN (SELECT id FROM usuarios)")
    suspend fun desvincularEmprendedorasSinCuenta()

    // Todo o nada: nunca sobrescribe la cuenta de otra persona que tenga el mismo id.
    @Transaction
    suspend fun guardarCopia(copia: UsuarioEntity) {
        if (borrarCopiasViejas(copia.id, copia.correo, copia.rutHash) > 0) desvincularEmprendedorasSinCuenta()
        if (buscarPorId(copia.id) == null) {
            insertar(copia)
        } else {
            actualizarCopia(
                id = copia.id,
                nombre = copia.nombre,
                correo = copia.correo,
                claveHash = copia.claveHash,
                sal = copia.sal,
                rol = copia.rol,
                estado = copia.estado,
                telefono = copia.telefono,
                region = copia.region,
                comuna = copia.comuna,
            )
        }
    }

    @Query(
        "UPDATE usuarios SET nombre = :nombre, correo = :correo, claveHash = :claveHash, sal = :sal, " +
                "rol = :rol, estado = :estado, telefono = :telefono, region = :region, comuna = :comuna WHERE id = :id"
    )
    suspend fun actualizarCopia(
        id: Long,
        nombre: String,
        correo: String,
        claveHash: String,
        sal: String,
        rol: Rol,
        estado: EstadoCuenta,
        telefono: String?,
        region: String?,
        comuna: String?,
    )
}
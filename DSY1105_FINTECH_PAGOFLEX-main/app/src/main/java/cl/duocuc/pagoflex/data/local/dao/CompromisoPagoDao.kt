package cl.duocuc.pagoflex.data.local.dao

import androidx.room.*
import cl.duocuc.pagoflex.data.model.CompromisoPago
import kotlinx.coroutines.flow.Flow

@Dao
interface CompromisoPagoDao {
    @Query("SELECT * FROM compromisos")
    fun getAllCompromisos(): Flow<List<CompromisoPago>>

    @Query("SELECT * FROM compromisos WHERE id = :id")
    suspend fun getCompromisoById(id: Int): CompromisoPago?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompromiso(compromiso: CompromisoPago)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(compromisos: List<CompromisoPago>)

    @Update
    suspend fun updateCompromiso(compromiso: CompromisoPago)

    @Query("UPDATE compromisos SET estado = :nuevoEstado WHERE id = :id")
    suspend fun updateEstado(id: Int, nuevoEstado: String)
}

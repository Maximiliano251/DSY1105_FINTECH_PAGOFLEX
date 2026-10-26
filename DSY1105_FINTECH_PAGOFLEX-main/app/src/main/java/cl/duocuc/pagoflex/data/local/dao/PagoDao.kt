package cl.duocuc.pagoflex.data.local.dao

import androidx.room.*
import cl.duocuc.pagoflex.data.local.entity.Pago
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {
    @Query("SELECT * FROM pagos ORDER BY id DESC")
    fun getAllPagos(): Flow<List<Pago>>

    @Query("SELECT * FROM pagos WHERE id = :id")
    suspend fun getPagoById(id: Int): Pago?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPago(pago: Pago): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pagos: List<Pago>)
}

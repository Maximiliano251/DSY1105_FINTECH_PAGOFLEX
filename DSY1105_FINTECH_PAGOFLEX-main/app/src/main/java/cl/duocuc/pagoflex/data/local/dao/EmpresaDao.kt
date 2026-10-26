package cl.duocuc.pagoflex.data.local.dao

import androidx.room.*
import cl.duocuc.pagoflex.data.local.entity.Empresa
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpresaDao {
    @Query("SELECT * FROM empresas")
    fun getAllEmpresas(): Flow<List<Empresa>>

    @Query("SELECT * FROM empresas WHERE id = :id")
    suspend fun getEmpresaById(id: Int): Empresa?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpresa(empresa: Empresa)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(empresas: List<Empresa>)
}

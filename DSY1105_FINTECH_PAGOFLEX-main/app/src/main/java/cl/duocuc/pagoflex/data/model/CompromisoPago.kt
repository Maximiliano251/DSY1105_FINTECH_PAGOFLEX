package cl.duocuc.pagoflex.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compromisos")
data class CompromisoPago(
    val empresa: String,
    val monto: Int,
    val fechaVencimiento: String,
    val estado: String,
    val numeroCliente: String = "9876543-2",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

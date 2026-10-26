package cl.duocuc.pagoflex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pagos")
data class Pago(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val compromisoId: Int = 0,
    val empresa: String,
    val monto: Int,
    val fechaPago: String,
    val comprobante: String,
    val estado: String = "Aprobado",
    val medioPago: String = "Tarjeta de Débito •••• 4321"
)

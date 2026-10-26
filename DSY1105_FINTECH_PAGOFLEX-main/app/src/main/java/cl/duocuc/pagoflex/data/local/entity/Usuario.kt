package cl.duocuc.pagoflex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val id: Int = 1,
    val nombre: String,
    val email: String,
    val rut: String,
    val telefono: String,
    val puntos: Int,
    val racha: Int,
    val nivel: String,
    val avisosActivos: Boolean = true,
    val diasAnticipacion: Int = 3,
    val fotoPerfilUri: String? = null
)
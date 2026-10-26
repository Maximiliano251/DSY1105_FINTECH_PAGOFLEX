package cl.duocuc.pagoflex.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duocuc.pagoflex.data.local.dao.*
import cl.duocuc.pagoflex.data.local.entity.*
import cl.duocuc.pagoflex.data.model.CompromisoPago
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Usuario::class,
        Empresa::class,
        CompromisoPago::class,
        Pago::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun empresaDao(): EmpresaDao
    abstract fun compromisoPagoDao(): CompromisoPagoDao
    abstract fun pagoDao(): PagoDao

    suspend fun populateDatabase() {
        // Anexo 9: Inserción de datos iniciales

        // 1. Usuarios
        val usuarioPrincipal = Usuario(
            id = 1,
            nombre = "Juan Pérez",
            email = "juan.perez@duocuc.cl",
            puntos = 1250,
            racha = 6,
            nivel = "Plata"
        )
        usuarioDao().insertUsuario(usuarioPrincipal)

        // 2. Empresas
        val empresas = listOf(
            Empresa(id = 1, nombre = "Aguas Andinas", categoria = "Agua Potable", rut = "96.809.000-4"),
            Empresa(id = 2, nombre = "Luz del Sur", categoria = "Electricidad", rut = "88.111.222-3"),
            Empresa(id = 3, nombre = "Metrogas", categoria = "Gas Natural", rut = "96.732.000-8"),
            Empresa(id = 4, nombre = "VTR", categoria = "Telecomunicaciones", rut = "96.834.000-7")
        )
        empresaDao().insertAll(empresas)

        // 3. Compromisos de Pago
        val compromisos = listOf(
            CompromisoPago(
                id = 1,
                empresa = "Aguas Andinas",
                numeroCliente = "9876543-2",
                monto = 24500,
                fechaVencimiento = "12 Oct 2026",
                estado = "Pendiente"
            ),
            CompromisoPago(
                id = 2,
                empresa = "Luz del Sur",
                numeroCliente = "1234567-8",
                monto = 18000,
                fechaVencimiento = "20 Oct 2026",
                estado = "Pagado"
            ),
            CompromisoPago(
                id = 3,
                empresa = "Metrogas",
                numeroCliente = "5554443-1",
                monto = 32000,
                fechaVencimiento = "28 Oct 2026",
                estado = "Pendiente"
            )
        )
        compromisoPagoDao().insertAll(compromisos)

        // 4. Historial de Pagos
        val historialPagos = listOf(
            Pago(
                id = 1,
                compromisoId = 2,
                empresa = "Luz del Sur",
                monto = 18000,
                fechaPago = "06 Oct 2026 - 18:55",
                comprobante = "#789456",
                estado = "Aprobado"
            )
        )
        pagoDao().insertAll(historialPagos)
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pagoflex_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context.applicationContext).populateDatabase()
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

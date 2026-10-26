package cl.duocuc.pagoflex.data.repository

import cl.duocuc.pagoflex.data.local.AppDatabase
import cl.duocuc.pagoflex.data.local.dao.*
import cl.duocuc.pagoflex.data.local.entity.*
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.util.RecargoInfo
import cl.duocuc.pagoflex.util.ReglasNegocioUtils
import cl.duocuc.pagoflex.util.ValidacionOrdenPago
import kotlinx.coroutines.flow.Flow

class PagoFlexRepository(
    private val usuarioDao: UsuarioDao,
    private val empresaDao: EmpresaDao,
    private val compromisoPagoDao: CompromisoPagoDao,
    private val pagoDao: PagoDao
) {
    // Usuario
    fun getUsuario(): Flow<Usuario?> = usuarioDao.getUsuario()
    suspend fun insertUsuario(usuario: Usuario) = usuarioDao.insertUsuario(usuario)
    suspend fun updateUsuario(usuario: Usuario) = usuarioDao.updateUsuario(usuario)
    suspend fun sumarPuntosYRacha(usuarioId: Int = 1) = usuarioDao.sumarPuntosYRacha(usuarioId)

    // Empresas
    fun getEmpresas(): Flow<List<Empresa>> = empresaDao.getAllEmpresas()
    suspend fun insertEmpresa(empresa: Empresa) = empresaDao.insertEmpresa(empresa)

    // Compromisos de Pago
    fun getCompromisos(): Flow<List<CompromisoPago>> = compromisoPagoDao.getAllCompromisos()
    suspend fun getCompromisoById(id: Int) = compromisoPagoDao.getCompromisoById(id)
    suspend fun insertCompromiso(compromiso: CompromisoPago) = compromisoPagoDao.insertCompromiso(compromiso)
    suspend fun updateEstadoCompromiso(id: Int, nuevoEstado: String) = compromisoPagoDao.updateEstado(id, nuevoEstado)

    // Historial de Pagos
    fun getPagos(): Flow<List<Pago>> = pagoDao.getAllPagos()
    suspend fun registrarPago(pago: Pago): Long = pagoDao.insertPago(pago)

    // Reglas de Negocio Delegadas
    fun calcularRecargo(compromiso: CompromisoPago): RecargoInfo =
        ReglasNegocioUtils.calcularRecargo(compromiso)

    fun validarOrdenDePago(
        compromiso: CompromisoPago,
        todosLosCompromisos: List<CompromisoPago>
    ): ValidacionOrdenPago = ReglasNegocioUtils.validarOrdenDePago(compromiso, todosLosCompromisos)

    fun calcularPorcentajeCumplimiento(pagos: List<Pago>): Double =
        ReglasNegocioUtils.calcularPorcentajeCumplimiento(pagos)

    companion object {
        @Volatile
        private var INSTANCE: PagoFlexRepository? = null

        fun getInstance(database: AppDatabase): PagoFlexRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = PagoFlexRepository(
                    database.usuarioDao(),
                    database.empresaDao(),
                    database.compromisoPagoDao(),
                    database.pagoDao()
                )
                INSTANCE = instance
                instance
            }
        }

        // Datos en memoria como fallback de respaldo
        val compromisos = listOf(
            CompromisoPago("Aguas Andinas", 24500, "12 Oct 2026", "Pendiente"),
            CompromisoPago("Luz del Sur", 18000, "20 Oct 2026", "Pagado")
        )
    }
}

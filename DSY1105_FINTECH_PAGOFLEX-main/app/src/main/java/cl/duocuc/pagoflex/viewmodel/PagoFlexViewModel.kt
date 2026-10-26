package cl.duocuc.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.pagoflex.data.local.AppDatabase
import cl.duocuc.pagoflex.data.local.entity.Empresa
import cl.duocuc.pagoflex.data.local.entity.Pago
import cl.duocuc.pagoflex.data.local.entity.Usuario
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.data.repository.PagoFlexRepository
import cl.duocuc.pagoflex.util.RecargoInfo
import cl.duocuc.pagoflex.util.ReglasNegocioUtils
import cl.duocuc.pagoflex.util.ValidacionOrdenPago
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ResultadoPagoState {
    data class Exito(val pago: Pago, val recargoInfo: RecargoInfo) : ResultadoPagoState()
    data class BloqueadoRN04(val motivo: String) : ResultadoPagoState()
    data class Error(val mensaje: String) : ResultadoPagoState()
}

class PagoFlexViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PagoFlexRepository

    init {
        val database = AppDatabase.getInstance(application)
        repository = PagoFlexRepository.getInstance(database)
    }

    val usuario: StateFlow<Usuario?> = repository.getUsuario()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val puntos: StateFlow<Int> = usuario.map { it?.puntos ?: 1250 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1250)

    val racha: StateFlow<Int> = usuario.map { it?.racha ?: 6 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)

    val compromisos: StateFlow<List<CompromisoPago>> = repository.getCompromisos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pagos: StateFlow<List<Pago>> = repository.getPagos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val porcentajeCumplimiento: StateFlow<Double> = pagos
        .map { repository.calcularPorcentajeCumplimiento(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100.0)

    private val _ultimoPago = MutableStateFlow<Pago?>(null)
    val ultimoPago: StateFlow<Pago?> = _ultimoPago.asStateFlow()

    fun obtenerRecargoInfo(compromiso: CompromisoPago): RecargoInfo =
        repository.calcularRecargo(compromiso)

    fun procesarPagoSimulado(
        compromiso: CompromisoPago,
        medioPago: String = "Tarjeta de Débito •••• 4321",
        onResultado: (ResultadoPagoState) -> Unit = {}
    ) {
        viewModelScope.launch {
            val lista = repository.getCompromisos().first()
            val validacion = repository.validarOrdenDePago(compromiso, lista)

            if (validacion is ValidacionOrdenPago.Bloqueado) {
                onResultado(ResultadoPagoState.BloqueadoRN04(validacion.motivo))
                return@launch
            }

            val recargoInfo = repository.calcularRecargo(compromiso)
            val nuevoPago = Pago(
                compromisoId = compromiso.id,
                empresa = compromiso.empresa,
                monto = recargoInfo.montoTotal,
                fechaPago = ReglasNegocioUtils.fechaActualFormateada(),
                comprobante = ReglasNegocioUtils.generarComprobanteUnico(),
                estado = "Aprobado",
                medioPago = medioPago
            )

            repository.registrarPago(nuevoPago)
            if (compromiso.id != 0) repository.updateEstadoCompromiso(compromiso.id, "Pagado")
            repository.sumarPuntosYRacha(usuarioId = 1)

            _ultimoPago.value = nuevoPago
            onResultado(ResultadoPagoState.Exito(nuevoPago, recargoInfo))
        }
    }

    // RF-09: El usuario reporta un problema
    fun reportarProblema(compromiso: CompromisoPago) {
        viewModelScope.launch {
            repository.updateEstadoCompromiso(compromiso.id, "En revisión")
        }
    }

    // RF-14: El agente resuelve el problema
    fun resolverProblema(compromiso: CompromisoPago) {
        viewModelScope.launch {
            // Para el prototipo, lo devolvemos a "Pendiente" como si se hubiera solucionado
            repository.updateEstadoCompromiso(compromiso.id, "Pendiente")
        }
    }
    fun anularCompromiso(compromiso: CompromisoPago) {
        viewModelScope.launch {
            repository.updateEstadoCompromiso(compromiso.id, "Anulado")
        }
    }
    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repository.updateUsuario(usuario) // Maxi ya tiene esta función en su Repository
        }
    }
}
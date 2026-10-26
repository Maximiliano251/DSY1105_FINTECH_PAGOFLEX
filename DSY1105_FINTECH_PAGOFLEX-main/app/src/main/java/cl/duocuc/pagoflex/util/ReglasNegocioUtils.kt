package cl.duocuc.pagoflex.util

import cl.duocuc.pagoflex.data.local.entity.Pago
import cl.duocuc.pagoflex.data.model.CompromisoPago
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round

data class RecargoInfo(
    val diasAtraso: Int,
    val recargo: Int,
    val montoOriginal: Int,
    val montoTotal: Int
)

sealed class ValidacionOrdenPago {
    object Permitido : ValidacionOrdenPago()
    data class Bloqueado(val motivo: String, val compromisoAnterior: CompromisoPago) : ValidacionOrdenPago()
}

object ReglasNegocioUtils {

    private val formatosFecha = listOf(
        SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es-ES")),
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    fun parseFecha(fechaStr: String): Date? {
        for (format in formatosFecha) {
            try {
                val date = format.parse(fechaStr)
                if (date != null) return date
            } catch (_: Exception) {
            }
        }
        return null
    }

    /**
     * RN-02 y RN-03 (Recargos):
     * Calcula los días de atraso y suma el recargo (ej. recargo fijo de $2.000 del Club Los Halcones)
     * si el compromiso está vencido.
     */
    fun calcularRecargo(compromiso: CompromisoPago, recargoFijo: Int = 2000): RecargoInfo {
        val fechaVenc = parseFecha(compromiso.fechaVencimiento)
        val hoy = Calendar.getInstance().time

        val esEstadoVencido = compromiso.estado.equals("Vencido", ignoreCase = true)
        val esFechaVencida = (fechaVenc != null) && hoy.after(fechaVenc)

        if (esFechaVencida && fechaVenc != null) {
            val diffMs = hoy.time - fechaVenc.time
            val dias = (diffMs / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
            return RecargoInfo(
                diasAtraso = dias,
                recargo = recargoFijo,
                montoOriginal = compromiso.monto,
                montoTotal = compromiso.monto + recargoFijo
            )
        } else if (esEstadoVencido) {
            return RecargoInfo(
                diasAtraso = 1,
                recargo = recargoFijo,
                montoOriginal = compromiso.monto,
                montoTotal = compromiso.monto + recargoFijo
            )
        }

        return RecargoInfo(
            diasAtraso = 0,
            recargo = 0,
            montoOriginal = compromiso.monto,
            montoTotal = compromiso.monto
        )
    }

    /**
     * RN-04 (Orden de pago):
     * Lógica para verificar si el usuario tiene cuotas anteriores vencidas
     * de la misma empresa y bloquear el pago de cuotas más nuevas.
     */
    fun validarOrdenDePago(
        compromisoAProcesar: CompromisoPago,
        todosLosCompromisos: List<CompromisoPago>
    ): ValidacionOrdenPago {
        val pendientesMismaEmpresa = todosLosCompromisos.filter { c ->
            c.empresa.equals(compromisoAProcesar.empresa, ignoreCase = true) &&
            c.id != compromisoAProcesar.id &&
            (c.estado.equals("Pendiente", ignoreCase = true) || c.estado.equals("Vencido", ignoreCase = true))
        }

        val fechaTarget = parseFecha(compromisoAProcesar.fechaVencimiento)

        val cuotaAnteriorPendiente = pendientesMismaEmpresa.find { pendiente ->
            val fechaPendiente = parseFecha(pendiente.fechaVencimiento)
            if (fechaPendiente != null && fechaTarget != null) {
                fechaPendiente.before(fechaTarget)
            } else {
                pendiente.id < compromisoAProcesar.id
            }
        }

        if (cuotaAnteriorPendiente != null) {
            return ValidacionOrdenPago.Bloqueado(
                motivo = "No se puede procesar el pago. Se detectó una cuota anterior pendiente/vencida de ${compromisoAProcesar.empresa} (${cuotaAnteriorPendiente.fechaVencimiento}). Debe saldar primero la cuota más antigua.",
                compromisoAnterior = cuotaAnteriorPendiente
            )
        }

        return ValidacionOrdenPago.Permitido
    }

    /**
     * RN-10 (Indicador de Cumplimiento):
     * Función matemática que calcula el % de pagos a tiempo sobre el total de los últimos 12 meses.
     * Fórmula: (% Cumplimiento = (Pagos a tiempo / Total pagos) * 100)
     */
    fun calcularPorcentajeCumplimiento(pagos: List<Pago>): Double {
        if (pagos.isEmpty()) return 100.0

        val pagosATiempo = pagos.count { p ->
            p.estado.equals("Aprobado", ignoreCase = true) ||
            p.estado.equals("A Tiempo", ignoreCase = true) ||
            p.estado.equals("Pagado", ignoreCase = true)
        }

        val porcentaje = (pagosATiempo.toDouble() / pagos.size.toDouble()) * 100.0
        return round(porcentaje * 10.0) / 10.0
    }

    /**
     * RF-05: Genera un número de comprobante único con el formato CPR-XXXX
     */
    fun generarComprobanteUnico(): String {
        val numeroAleatorio = (1000..9999).random()
        return "CPR-$numeroAleatorio"
    }

    /**
     * Formatea la fecha actual para el registro del pago
     */
    fun fechaActualFormateada(): String {
        val format = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.forLanguageTag("es-ES"))
        return format.format(Date())
    }
}

package cl.duocuc.pagoflex

import cl.duocuc.pagoflex.data.local.entity.Pago
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.util.ReglasNegocioUtils
import cl.duocuc.pagoflex.util.ValidacionOrdenPago
import org.junit.Assert.*
import org.junit.Test

class ReglasNegocioTest {

    @Test
    fun testRN02yRN03_RecargoPorMora() {
        // Compromiso no vencido
        val compromisoAlDia = CompromisoPago(
            id = 1,
            empresa = "Aguas Andinas",
            monto = 24500,
            fechaVencimiento = "20 Dec 2030",
            estado = "Pendiente"
        )
        val recargoAlDia = ReglasNegocioUtils.calcularRecargo(compromisoAlDia)
        assertEquals(0, recargoAlDia.recargo)
        assertEquals(24500, recargoAlDia.montoTotal)

        // Compromiso vencido por estado
        val compromisoVencido = CompromisoPago(
            id = 2,
            empresa = "Club Los Halcones",
            monto = 15000,
            fechaVencimiento = "01 Oct 2020",
            estado = "Vencido"
        )
        val recargoVencido = ReglasNegocioUtils.calcularRecargo(compromisoVencido, recargoFijo = 2000)
        assertEquals(2000, recargoVencido.recargo)
        assertEquals(17000, recargoVencido.montoTotal)
        assertTrue(recargoVencido.diasAtraso > 0)
    }

    @Test
    fun testRN04_BloqueoPorCuotaAnteriorVencida() {
        val listaCompromisos = listOf(
            CompromisoPago(id = 1, empresa = "Aguas Andinas", monto = 20000, fechaVencimiento = "10 Sep 2026", estado = "Pendiente"),
            CompromisoPago(id = 2, empresa = "Aguas Andinas", monto = 24500, fechaVencimiento = "10 Oct 2026", estado = "Pendiente")
        )

        // Intento de pagar cuota 2 teniendo cuota 1 pendiente
        val resultado = ReglasNegocioUtils.validarOrdenDePago(listaCompromisos[1], listaCompromisos)
        assertTrue(resultado is ValidacionOrdenPago.Bloqueado)

        // Intento de pagar cuota 1 (la primera de esa empresa)
        val resultadoPrimera = ReglasNegocioUtils.validarOrdenDePago(listaCompromisos[0], listaCompromisos)
        assertTrue(resultadoPrimera is ValidacionOrdenPago.Permitido)
    }

    @Test
    fun testRN10_CalculoPorcentajeCumplimiento() {
        val listaPagos = listOf(
            Pago(id = 1, empresa = "Luz del Sur", monto = 18000, fechaPago = "2026-10-01", comprobante = "CPR-1111", estado = "Aprobado"),
            Pago(id = 2, empresa = "Metrogas", monto = 30000, fechaPago = "2026-10-02", comprobante = "CPR-2222", estado = "Aprobado")
        )

        val porcentaje = ReglasNegocioUtils.calcularPorcentajeCumplimiento(listaPagos)
        assertEquals(100.0, porcentaje, 0.01)
    }

    @Test
    fun testRF05_FormatoComprobanteUnico() {
        val comprobante = ReglasNegocioUtils.generarComprobanteUnico()
        assertTrue(comprobante.startsWith("CPR-"))
        assertEquals(8, comprobante.length) // "CPR-" + 4 dígitos = 8 caracteres
    }
}

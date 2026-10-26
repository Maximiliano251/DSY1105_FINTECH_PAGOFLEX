package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.ui.components.BotonPrincipal
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion
import cl.duocuc.pagoflex.util.RecargoInfo
import cl.duocuc.pagoflex.util.ValidacionOrdenPago

@Composable
fun ConfirmarPagoScreen(
    compromiso: CompromisoPago?,
    recargoInfo: RecargoInfo?,
    validacionOrden: ValidacionOrdenPago?,
    onConfirmarPago: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Confirmar Pago", style = MaterialTheme.typography.headlineMedium)

        if (compromiso != null) {
            TarjetaSeccion(titulo = "Resumen") {
                Text("Servicio: ${compromiso.empresa}")
                val montoFinal = recargoInfo?.montoTotal ?: compromiso.monto
                Text("Total a Pagar: $ ${String.format(java.util.Locale("es", "CL"), "%,d", montoFinal)}", style = MaterialTheme.typography.titleLarge)
            }

            if (validacionOrden is ValidacionOrdenPago.Bloqueado) {
                Text(validacionOrden.motivo, color = MaterialTheme.colorScheme.error)
            } else {
                BotonPrincipal(
                    texto = "Confirmar Pago",
                    onClick = { onConfirmarPago("Tarjeta de Débito") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        BotonSecundario(texto = "Cancelar", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
}
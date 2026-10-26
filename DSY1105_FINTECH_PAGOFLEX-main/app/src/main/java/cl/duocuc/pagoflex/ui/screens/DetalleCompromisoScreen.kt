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

@Composable
fun DetalleCompromisoScreen(
    compromiso: CompromisoPago?,
    recargoInfo: RecargoInfo?,
    onNavigateToConfirmarPago: () -> Unit,
    onNavigateToReportarProblema: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Detalle del Compromiso", style = MaterialTheme.typography.headlineMedium)

        if (compromiso != null) {
            TarjetaSeccion(titulo = "Información del Servicio") {
                Text("Empresa: ${compromiso.empresa}")
                Text(
                    "Monto Base: $ ${
                        String.format(
                            java.util.Locale("es", "CL"),
                            "%,d",
                            compromiso.monto
                        )
                    }"
                )
                Text("Vencimiento: ${compromiso.fechaVencimiento}")

                // Si hay recargo, lo mostramos
                // Solo mostramos el recargo si existe, es mayor a 0, Y EL COMPROMISO NO ESTÁ PAGADO.
                if (recargoInfo != null && recargoInfo.recargo > 0 && compromiso.estado != "Pagado" && compromiso.estado != "Anulado") {
                    Text(
                        "Recargo por atraso: $ ${
                            String.format(
                                java.util.Locale("es", "CL"),
                                "%,d",
                                recargoInfo.recargo
                            )
                        }", color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        "Total a Pagar: $ ${
                            String.format(
                                java.util.Locale("es", "CL"),
                                "%,d",
                                recargoInfo.montoTotal
                            )
                        }", style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    // Si está pagado o no tiene recargo, mostramos el monto base o el total real pagado
                    val total =
                        if (compromiso.estado == "Pagado") compromiso.monto else recargoInfo?.montoTotal
                            ?: compromiso.monto
                    Text(
                        "Total: $ ${String.format(java.util.Locale("es", "CL"), "%,d", total)}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                BotonPrincipal(
                    texto = "Pagar Ahora",
                    onClick = onNavigateToConfirmarPago,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonSecundario(
                    texto = "Reportar un Problema con este Cobro",
                    onClick = onNavigateToReportarProblema,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            BotonSecundario(texto = "Volver", onClick = onBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
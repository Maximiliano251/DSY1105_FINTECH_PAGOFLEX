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

@Composable
fun ReportarProblemaScreen(
    compromiso: CompromisoPago?,
    onReportar: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Reportar un Problema", style = MaterialTheme.typography.headlineMedium)

        if (compromiso != null) {
            TarjetaSeccion(titulo = "Compromiso a reportar") {
                Text("Empresa: ${compromiso.empresa}")
                Text("Monto: $ ${compromiso.monto}")
                Text("Estado actual: ${compromiso.estado}")
            }

            Text("Al reportar, el compromiso pasará a estado 'En revisión' y no se podrá pagar hasta que un agente lo resuelva.", style = MaterialTheme.typography.bodyMedium)

            BotonPrincipal(
                texto = "Confirmar Reporte (Ya pagué / Monto incorrecto)",
                onClick = onReportar,
                modifier = Modifier.fillMaxWidth()
            )
        }
        BotonSecundario(texto = "Cancelar", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
}
package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.viewmodel.PagoFlexViewModel

@Composable
fun HistorialScreen(
    viewModel: PagoFlexViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagos by viewModel.pagos.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial de Pagos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (pagos.isEmpty()) {
            Text("Aún no tienes pagos registrados.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(pagos) { pago ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Empresa: ${pago.empresa}", style = MaterialTheme.typography.titleMedium)
                            Text("Monto: $ ${pago.monto}")
                            Text("Comprobante: ${pago.comprobante}")
                            Text("Fecha: ${pago.fechaPago}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonSecundario(texto = "Volver al Inicio", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
}
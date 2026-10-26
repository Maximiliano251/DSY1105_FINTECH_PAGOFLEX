package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.local.entity.Pago
import cl.duocuc.pagoflex.ui.components.BotonPrincipal
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun PagoExitosoScreen(
    pago: Pago?,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("¡Pago Exitoso! 🎉", style = MaterialTheme.typography.headlineMedium)

        if (pago != null) {
            TarjetaSeccion(titulo = "Comprobante ${pago.comprobante}") {
                Text("Empresa: ${pago.empresa}")
                Text("Monto Pagado: $ ${String.format(java.util.Locale("es", "CL"), "%,d", pago.monto)}")
                Text("Fecha: ${pago.fechaPago}")
                Text("Medio: ${pago.medioPago}")
            }
        }

        BotonPrincipal(texto = "Volver al Inicio", onClick = onNavigateToHome, modifier = Modifier.fillMaxWidth())
    }
}
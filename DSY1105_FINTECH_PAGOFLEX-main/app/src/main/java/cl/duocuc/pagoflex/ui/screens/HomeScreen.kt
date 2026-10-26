package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.ui.components.BotonPrincipal
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaEstadistica
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun HomeScreen(
    listaCompromisos: List<CompromisoPago>,
    onNavigateToDetalle: (CompromisoPago) -> Unit,
    onNavigateToHistorial: () -> Unit,
    onNavigateToRecompensas: () -> Unit,
    onNavigateToReferidos: () -> Unit,
    modifier: Modifier = Modifier,
    puntos: Int,
    racha: Int,
    porcentajeCumplimiento: Double
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Hola, Camila Rojas", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaEstadistica(valor = "%,d".format(puntos), "Puntos Flex", Modifier.weight(1f))
            TarjetaEstadistica(valor = "$racha días", "Racha", Modifier.weight(1f))
            TarjetaEstadistica(valor = "%.0f%%".format(porcentajeCumplimiento), "Cumplimiento", Modifier.weight(1f))
        }

        BotonSecundario(texto = "Ver mi Historial de Pagos", onClick = onNavigateToHistorial, modifier = Modifier.fillMaxWidth())

        Text("Mis Compromisos", style = MaterialTheme.typography.titleLarge)

        if (listaCompromisos.isEmpty()) {
            Text("No tienes compromisos pendientes.")
        } else {
            listaCompromisos.forEach { compromiso ->
                TarjetaSeccion(titulo = "Deuda con ${compromiso.empresa}") {
                    // RNF-02: Formato de moneda chileno
                    Text("Monto: $ ${String.format(java.util.Locale("es", "CL"), "%,d", compromiso.monto)}", style = MaterialTheme.typography.bodyLarge)
                    Text("Vence: ${compromiso.fechaVencimiento}", style = MaterialTheme.typography.bodyMedium)
                    Text("Estado: ${compromiso.estado}", color = MaterialTheme.colorScheme.secondary)

                    Spacer(modifier = Modifier.height(8.dp))

                    BotonPrincipal(
                        texto = "Ver Detalle y Pagar",
                        onClick = { onNavigateToDetalle(compromiso) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        TarjetaSeccion(titulo = "Accesos Rápidos") {
            BotonSecundario(texto = "Ver Recompensas y Logros", onClick = onNavigateToRecompensas, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))
            BotonSecundario(texto = "Invita y Gana (Referidos)", onClick = onNavigateToReferidos, modifier = Modifier.fillMaxWidth())
        }
    }
}
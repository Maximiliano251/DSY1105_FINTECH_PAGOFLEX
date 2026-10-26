package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun EjecutivoScreen(
    listaCompromisos: List<CompromisoPago>,
    onAnular: (CompromisoPago) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filtramos los que no están pagados ni anulados
    val compromisosActivos = listaCompromisos.filter { it.estado != "Pagado" && it.estado != "Anulado" }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Panel de Cobranza", style = MaterialTheme.typography.headlineMedium)
        Text("Ejecutivo: Paula Saavedra (Altos del Maipo)", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        TarjetaSeccion(titulo = "Resumen del Mes (RF-20)") {
            Text("Total Compromisos: ${compromisosActivos.size}")
            Text("Recaudación Estimada: $ ${compromisosActivos.sumOf { it.monto }}")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Gestión de Compromisos", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (compromisosActivos.isEmpty()) {
            Text("No hay compromisos pendientes.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(compromisosActivos) { compromiso ->
                    TarjetaSeccion(titulo = compromiso.empresa) {
                        Text("Monto: $ ${compromiso.monto}")
                        Text("Estado: ${compromiso.estado}")
                        Spacer(modifier = Modifier.height(8.dp))
                        // Botón para anular (RF-19)
                        BotonSecundario(
                            texto = "Anular Compromiso",
                            onClick = { onAnular(compromiso) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonSecundario(texto = "Cerrar Sesión de Ejecutivo", onClick = onVolver, modifier = Modifier.fillMaxWidth())
    }
}
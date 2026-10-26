package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.ui.components.BotonPrincipal
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun AgenteScreen(
    listaCompromisos: List<CompromisoPago>,
    onResolver: (CompromisoPago) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filtramos solo los que tienen reportes ("En revisión")
    val reportes = listaCompromisos.filter { it.estado == "En revisión" }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Panel del Agente PagoFlex", style = MaterialTheme.typography.headlineMedium)
        Text("Reportes Pendientes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (reportes.isEmpty()) {
            Text("No hay reportes pendientes. ¡Buen trabajo!")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(reportes) { compromiso ->
                    TarjetaSeccion(titulo = "Reporte: ${compromiso.empresa}") {
                        Text("Monto: $ ${compromiso.monto}")
                        Text("Estado: ${compromiso.estado}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        BotonPrincipal(
                            texto = "Resolver Reporte",
                            onClick = { onResolver(compromiso) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BotonSecundario(texto = "Cerrar Sesión de Agente", onClick = onVolver, modifier = Modifier.fillMaxWidth())
    }
}
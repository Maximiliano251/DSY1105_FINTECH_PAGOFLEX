package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaEstadistica
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun RecompensasScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    puntos: Int,
    racha: Int
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Recompensas y Logros",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TarjetaEstadistica(valor = "1,300", "Puntos Flex", Modifier.weight(1f))
            TarjetaEstadistica(valor = "Nivel 2", "Plata", Modifier.weight(1f))
        }

        TarjetaSeccion(titulo = "Logros Desbloqueados") {
            Text("Pagador Puntual: 5 pagos a tiempo seguidos", style = MaterialTheme.typography.bodyMedium)
            Text("Primeros Pasos: Cuenta creada y verificada", style = MaterialTheme.typography.bodyMedium)
        }

        TarjetaSeccion(titulo = "Próximas Recompensas") {
            Text("Descuento 10% en servicios (Faltan 200 pts)", style = MaterialTheme.typography.bodyMedium)
            Text("Bono de Racha de 10 días (Faltan 4 días)", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

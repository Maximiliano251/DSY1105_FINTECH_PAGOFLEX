package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.BotonPrincipal
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun ReferidosScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Invita y Gana",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        TarjetaSeccion(titulo = "Tu Código de Referido") {
            Text("Comparte este código con tus amigos y gana 100 Puntos Flex por cada uno que se registre.", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "REGISTRO2026-JUAN",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            BotonPrincipal(
                texto = "Compartir Código",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }

        TarjetaSeccion(titulo = "Tus Referidos") {
            Text("• María Silva - Registrada (100 pts ganados)", style = MaterialTheme.typography.bodyMedium)
            Text("• Carlos Soto - Pendiente de primer pago", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

package cl.duocuc.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duocuc.pagoflex.ui.components.BotonPrincipal

@Composable
fun LoginScreen(
    onLoginAsUser: () -> Unit,
    onLoginAsAgent: () -> Unit,
    onLoginAsExecutive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PagoFlex", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text("Selecciona un perfil para ingresar", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))

        // PERFIL 1: USUARIO FINAL
        TarjetaPerfil(
            inicial = "C",
            nombre = "Camila Rojas",
            rol = "Usuario Final (UF-01)",
            colorFondo = MaterialTheme.colorScheme.primary,
            onClick = onLoginAsUser
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PERFIL 2: AGENTE
        TarjetaPerfil(
            inicial = "A",
            nombre = "Andrés Lagos",
            rol = "Agente PagoFlex (AG-01)",
            colorFondo = MaterialTheme.colorScheme.secondary,
            onClick = onLoginAsAgent
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PERFIL 3: EJECUTIVO
        TarjetaPerfil(
            inicial = "P",
            nombre = "Paula Saavedra",
            rol = "Ejecutiva (EJ-01) - Altos del Maipo",
            colorFondo = MaterialTheme.colorScheme.tertiary,
            onClick = onLoginAsExecutive
        )
    }
}

@Composable
fun TarjetaPerfil(
    inicial: String,
    nombre: String,
    rol: String,
    colorFondo: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con la inicial del nombre
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorFondo),
                contentAlignment = Alignment.Center
            ) {
                Text(text = inicial, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = rol, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            BotonPrincipal(
                texto = "Entrar",
                onClick = onClick,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}
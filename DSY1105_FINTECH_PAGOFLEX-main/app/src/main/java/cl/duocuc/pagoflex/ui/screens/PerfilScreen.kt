package cl.duocuc.pagoflex.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cl.duocuc.pagoflex.data.local.entity.Usuario
import cl.duocuc.pagoflex.ui.components.BotonPrincipal
import cl.duocuc.pagoflex.ui.components.BotonSecundario
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion

@Composable
fun PerfilScreen(
    usuario: Usuario?,
    onGuardar: (Usuario) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var avisosActivos by remember { mutableStateOf(true) }
    var diasAnticipacion by remember { mutableStateOf("3") }

    // Estados para la foto
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            email = it.email
            telefono = it.telefono
            avisosActivos = it.avisosActivos
            diasAnticipacion = it.diasAnticipacion.toString()
            if (it.fotoPerfilUri != null) fotoUri = Uri.parse(it.fotoPerfilUri)
        }
    }

    // LAUNCHERS DE PERMISOS Y ACCIONES
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            fotoUri = uri
            fotoBitmap = null // Limpiamos la cámara si había una
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            fotoBitmap = bitmap
            fotoUri = null // Limpiamos la galería si había una
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) cameraLauncher.launch(null)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) avisosActivos = true else avisosActivos = false
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)

        // --- SECCIÓN DEL AVATAR ---
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable { /* Aquí podrías abrir un menú de opciones */ },
            contentAlignment = Alignment.Center
        ) {
            if (fotoUri != null) {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (fotoBitmap != null) {
                Image(
                    bitmap = fotoBitmap!!.asImageBitmap(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Sin foto",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonSecundario(
                texto = "Galería",
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            )
            BotonSecundario(
                texto = "Cámara",
                onClick = {
                    // Pedimos permiso de cámara antes de abrirla
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f)
            )
        }

        val rutMostrar = usuario?.rut?.replaceRange(3, 8, "*.*.*") ?: "15.*.*-3"
        Text("RUT: $rutMostrar", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

        // --- DATOS PERSONALES ---
        TarjetaSeccion(titulo = "Datos Personales") {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- CONFIGURACIÓN DE AVISOS (Notificaciones) ---
        TarjetaSeccion(titulo = "Configuración de Avisos") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Recibir avisos de vencimiento")
                Switch(
                    checked = avisosActivos,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            // Si intenta activar, pedimos permiso de notificaciones (Android 13+)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                avisosActivos = true
                            }
                        } else {
                            avisosActivos = false
                        }
                    }
                )
            }
            if (avisosActivos) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Días de anticipación (1, 3 o 5):")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1", "3", "5").forEach { dia ->
                        FilterChip(
                            selected = diasAnticipacion == dia,
                            onClick = { diasAnticipacion = dia },
                            label = { Text(dia) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BotonPrincipal(
            texto = "Guardar Cambios",
            onClick = {
                if (usuario != null) {
                    // Guardamos la URI de la galería (si se usó cámara, guardamos un texto de prueba por simplicidad)
                    val uriString = fotoUri?.toString() ?: if (fotoBitmap != null) "camara_local" else null

                    val usuarioActualizado = usuario.copy(
                        nombre = nombre,
                        email = email,
                        telefono = telefono,
                        avisosActivos = avisosActivos,
                        diasAnticipacion = diasAnticipacion.toIntOrNull() ?: 3,
                        fotoPerfilUri = uriString
                    )
                    onGuardar(usuarioActualizado)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        BotonSecundario(texto = "Volver", onClick = onBack, modifier = Modifier.fillMaxWidth())
    }
}
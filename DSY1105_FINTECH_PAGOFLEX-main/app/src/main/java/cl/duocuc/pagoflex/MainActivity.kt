package cl.duocuc.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.pagoflex.data.model.CompromisoPago
import cl.duocuc.pagoflex.ui.screens.*
import cl.duocuc.pagoflex.ui.theme.MiRegistroTheme
import cl.duocuc.pagoflex.util.ReglasNegocioUtils
import cl.duocuc.pagoflex.viewmodel.PagoFlexViewModel
import cl.duocuc.pagoflex.viewmodel.ResultadoPagoState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiRegistroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val vm: PagoFlexViewModel = viewModel()

                    var currentScreen by remember { mutableStateOf("login") }
                    var compromisoSeleccionado by remember { mutableStateOf<CompromisoPago?>(null) }

                    val compromisos by vm.compromisos.collectAsState()
                    val puntos by vm.puntos.collectAsState()
                    val racha by vm.racha.collectAsState()
                    val porcentajeCumplimiento by vm.porcentajeCumplimiento.collectAsState()
                    val ultimoPago by vm.ultimoPago.collectAsState()

                    val compromisoActual = compromisoSeleccionado ?: compromisos.firstOrNull()
                    val recargoInfo = compromisoActual?.let { vm.obtenerRecargoInfo(it) }
                    val validacionOrden = compromisoActual?.let { ReglasNegocioUtils.validarOrdenDePago(it, compromisos) }

                    val usuario by vm.usuario.collectAsState()

                    Column(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            // 2. AGREGAMOS LA PANTALLA DE LOGIN
                            "login" -> LoginScreen(
                                onLoginAsUser = { currentScreen = "home" },
                                onLoginAsAgent = { currentScreen = "agente" },
                                onLoginAsExecutive = { currentScreen = "ejecutivo" }
                            )

                            "home" -> HomeScreen(
                            listaCompromisos = compromisos,
                            puntos = puntos,
                            racha = racha,
                            porcentajeCumplimiento = porcentajeCumplimiento,
                            onNavigateToDetalle = { comp -> compromisoSeleccionado = comp; currentScreen = "detalle" },
                            onNavigateToHistorial = { currentScreen = "historial" },
                            onNavigateToRecompensas = { currentScreen = "recompensas" },
                            onNavigateToReferidos = { currentScreen = "referidos" },
                            onNavigateToPerfil = { currentScreen = "perfil" },
                            onLogout = { currentScreen = "login" }
                        )

                            "agente" -> AgenteScreen(
                                listaCompromisos = compromisos,
                                onResolver = { comp -> vm.resolverProblema(comp) },
                                onVolver = { currentScreen = "login" }
                            )

                            "ejecutivo" -> EjecutivoScreen(
                                listaCompromisos = compromisos,
                                onAnular = { comp -> vm.anularCompromiso(comp) },
                                onVolver = { currentScreen = "login" }
                            )

                            "historial" -> HistorialScreen(viewModel = vm, onBack = { currentScreen = "home" })

                            "detalle" -> DetalleCompromisoScreen(
                                compromiso = compromisoActual,
                                recargoInfo = recargoInfo,
                                onNavigateToConfirmarPago = { currentScreen = "confirmar" },
                                onNavigateToReportarProblema = { currentScreen = "reportar" },
                                onBack = { currentScreen = "home" }
                            )

                            "reportar" -> ReportarProblemaScreen(
                                compromiso = compromisoActual,
                                onReportar = { compromisoActual?.let { vm.reportarProblema(it) }; currentScreen = "home" },
                                onBack = { currentScreen = "detalle" }
                            )

                            "perfil" -> PerfilScreen(
                                usuario = usuario,
                                onGuardar = { usuarioActualizado ->
                                    vm.actualizarUsuario(usuarioActualizado)
                                    currentScreen = "home"
                                },
                                onBack = { currentScreen = "home" }
                            )

                            "confirmar" -> ConfirmarPagoScreen(
                                compromiso = compromisoActual,
                                recargoInfo = recargoInfo,
                                validacionOrden = validacionOrden,
                                onConfirmarPago = { medio ->
                                    compromisoActual?.let { comp ->
                                        vm.procesarPagoSimulado(comp, medio) { res ->
                                            if (res is ResultadoPagoState.Exito) currentScreen = "exito"
                                        }
                                    }
                                },
                                onBack = { currentScreen = "detalle" }
                            )

                            "exito" -> PagoExitosoScreen(pago = ultimoPago, onNavigateToHome = { currentScreen = "home" })

                            "recompensas" -> RecompensasScreen(puntos = puntos, racha = racha, onBack = { currentScreen = "home" })

                            "referidos" -> ReferidosScreen(onBack = { currentScreen = "home" })
                        }
                    }
                }
            }
        }
    }
}
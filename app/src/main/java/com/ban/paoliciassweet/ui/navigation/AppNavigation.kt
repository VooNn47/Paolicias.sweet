package com.ban.paoliciassweet.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.screen.AgendaScreen
import com.ban.paoliciassweet.ui.screen.BanqueteriaScreen
import com.ban.paoliciassweet.ui.screen.HomeScreen
import com.ban.paoliciassweet.ui.screen.LoginScreen
import com.ban.paoliciassweet.ui.screen.PanelAdminScreen
import com.ban.paoliciassweet.ui.screen.PerfilScreen
import com.ban.paoliciassweet.ui.screen.ProductoDetalleScreen
import com.ban.paoliciassweet.ui.screen.RegisterScreen
import com.ban.paoliciassweet.ui.screen.ReservaScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Routes {
    const val INICIO = "INICIO"
    const val LOGIN = "Login"
    const val REGISTER = "register"
    const val HOME = "Home"
    const val AGENDA = "Agenda"
    const val RESERVA = "Reserva"
    const val PERFIL = "Perfil"
    const val ADMIN = "Admin"

    const val PRODUCTO = "producto/{id}"
}

// Cambia a una pestaña del cliente sin apilar pantallas repetidas
private fun NavHostController.irA(ruta: String) {
    navigate(ruta) {
        popUpTo(Routes.HOME) { saveState = false }
        launchSingleTop = true
    }
}

// Vuelve al login borrando toda la navegación anterior (no se puede "volver" a una sesión cerrada)
private fun NavHostController.cerrarSesion() {
    Sesion.cerrar()
    navigate(Routes.LOGIN) {
        popUpTo(graph.id) { inclusive = true }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val demora = 500L // milisegundos de espera antes de cambiar de pantalla

    fun conDemora(accion: () -> Unit) {
        scope.launch {
            delay(demora)
            accion()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.INICIO,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Routes.INICIO) {
            BanqueteriaScreen(
                onNavigateToLogin = { conDemora { navController.navigate(Routes.LOGIN) } }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { conDemora { navController.navigate(Routes.REGISTER) } },
                onLoginSuccess = {
                    conDemora {
                        // Navegación según el rol guardado en SQLite
                        val destino = if (Sesion.esAdmin) Routes.ADMIN else Routes.HOME
                        navController.navigate(destino) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    conDemora {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onNavigateToBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            PantallaCliente(navController) {
                HomeScreen(
                    onProductoClick = { id -> navController.navigate("producto/$id") },
                    onNavigateToReserva = { conDemora { navController.irA(Routes.RESERVA) } },
                    onNavigateToAgenda = { conDemora { navController.irA(Routes.AGENDA) } },
                    onNavigateToPerfil = { conDemora { navController.irA(Routes.PERFIL) } }
                )
            }
        }

        composable(
            route = Routes.PRODUCTO,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            PantallaCliente(navController) {
                ProductoDetalleScreen(
                    productoId = id,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.AGENDA) {
            PantallaCliente(navController) {
                AgendaScreen(
                    onNavigateToHome = { conDemora { navController.irA(Routes.HOME) } },
                    onNavigateToReserva = { conDemora { navController.irA(Routes.RESERVA) } },
                    onNavigateToPerfil = { conDemora { navController.irA(Routes.PERFIL) } }
                )
            }
        }

        composable(Routes.RESERVA) {
            PantallaCliente(navController) {
                ReservaScreen(
                    onNavigateToHome = { conDemora { navController.irA(Routes.HOME) } },
                    onNavigateToAgenda = { conDemora { navController.irA(Routes.AGENDA) } },
                    onNavigateToPerfil = { conDemora { navController.irA(Routes.PERFIL) } }
                )
            }
        }

        composable(Routes.PERFIL) {
            PantallaCliente(navController) {
                PerfilScreen(
                    onNavigateToHome = { conDemora { navController.irA(Routes.HOME) } },
                    onNavigateToAgenda = { conDemora { navController.irA(Routes.AGENDA) } },
                    onNavigateToReserva = { conDemora { navController.irA(Routes.RESERVA) } },
                    onLogout = { conDemora { navController.cerrarSesion() } }
                )
            }
        }

        composable(Routes.ADMIN) {
            if (!Sesion.esAdmin) {
                // Solo la administradora puede ver el panel
                RedirigirAlLogin(navController)
            } else {
                PanelAdminScreen(
                    onLogout = { conDemora { navController.cerrarSesion() } }
                )
            }
        }
    }
}

// Protege las pantallas del cliente: sin sesión, o con sesión de administradora, vuelve al inicio que corresponde
@Composable
private fun PantallaCliente(navController: NavHostController, contenido: @Composable () -> Unit) {
    when {
        Sesion.correo.isBlank() -> RedirigirAlLogin(navController)
        Sesion.esAdmin -> LaunchedEffect(Unit) {
            navController.navigate(Routes.ADMIN) { popUpTo(navController.graph.id) { inclusive = true } }
        }
        else -> contenido()
    }
}

@Composable
private fun RedirigirAlLogin(navController: NavHostController) {
    LaunchedEffect(Unit) {
        navController.navigate(Routes.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
    }
}

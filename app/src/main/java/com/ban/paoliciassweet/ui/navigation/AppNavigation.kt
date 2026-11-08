package com.ban.paoliciassweet.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.ban.paoliciassweet.ui.viewmodel.AgendaViewmodel
import com.ban.paoliciassweet.ui.viewmodel.HomeViewModel
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val demora = 500L // milisegundos de espera antes de cambiar de pantalla

    NavHost(
        navController = navController,
        startDestination = Routes.INICIO,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Routes.INICIO) {
            BanqueteriaScreen(
                onNavigateToLogin = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.LOGIN)
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.REGISTER)
                    }
                },
                onLoginSuccess = {
                    scope.launch {
                        delay(demora)
                        val destino = if (Sesion.esAdmin) Routes.ADMIN else Routes.HOME
                        navController.navigate(destino) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onNavigateToBack = {
                    scope.launch {
                        delay(demora)
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val context = LocalContext.current
            val homeViewModel: HomeViewModel = viewModel()

            LaunchedEffect(Unit) {
                homeViewModel.cargarProductos(context)
            }

            HomeScreen(
                viewModel = homeViewModel,
                onProductoClick = { id ->
                    navController.navigate("producto/$id")
                },
                onNavigateToReserva = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.RESERVA) { launchSingleTop = true }
                    }
                },
                onNavigateToAgenda = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.AGENDA) { launchSingleTop = true }
                    }
                },
                onNavigateToPerfil = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.PERFIL) { launchSingleTop = true }
                    }
                }
            )
        }
        composable(
            route = Routes.PRODUCTO,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            ProductoDetalleScreen(
                productoId = id,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.AGENDA) {
            val context = LocalContext.current
            val agendaViewmodel: AgendaViewmodel = viewModel()

            LaunchedEffect(Unit) {
                agendaViewmodel.cargarReserva(context)
            }

            AgendaScreen(
                onNavigateToHome = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                },
                onNavigateToReserva = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.RESERVA) { launchSingleTop = true }
                    }
                },
                onNavigateToPerfil = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.PERFIL) { launchSingleTop = true }
                    }
                },
                agendaViewmodel = agendaViewmodel
            )
        }

        composable(Routes.RESERVA) {
            ReservaScreen(
                onNavigateToHome = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                },
                onNavigateToAgenda = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.AGENDA) { launchSingleTop = true }
                    }
                },
                onNavigateToPerfil = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.PERFIL) { launchSingleTop = true }
                    }
                }
            )
        }

        composable(Routes.PERFIL) {
            PerfilScreen(
                onNavigateToHome = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    }
                },
                onNavigateToAgenda = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.AGENDA) { launchSingleTop = true }
                    }
                },
                onNavigateToReserva = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.RESERVA) { launchSingleTop = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN) {
            PanelAdminScreen(
                onLogout = {
                    scope.launch {
                        delay(demora)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.ADMIN) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}


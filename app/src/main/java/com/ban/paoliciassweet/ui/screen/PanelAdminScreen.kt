package com.ban.paoliciassweet.ui.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.components.Footer
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.viewmodel.AdminViewModel

@Composable
fun PanelAdminScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val filtroActual = viewModel.uiState.filtro
    val reservasVisibles = viewModel.reservasFiltradas()

    LaunchedEffect(Unit) {
        viewModel.cargarReservas(context)
    }

    Scaffold(
        topBar = { Header(title = "PANEL ADMIN") },
        bottomBar = { Footer() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Cerrar sesión
            Button(
                onClick = {
                    Sesion.correo = ""
                    Sesion.esAdmin = false
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filtros por estado del abono (2 filas de 2 botones; el activo va en burdeos)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonFiltro("Todas", filtroActual, { viewModel.onFiltroChanged("Todas") }, Modifier.weight(1f))
                BotonFiltro("Pendiente", filtroActual, { viewModel.onFiltroChanged("Pendiente") }, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonFiltro("En revisión", filtroActual, { viewModel.onFiltroChanged("En revisión") }, Modifier.weight(1f))
                BotonFiltro("Pagado 50%", filtroActual, { viewModel.onFiltroChanged("Pagado 50%") }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mensaje si no hay reservas para el filtro elegido
            if (reservasVisibles.isEmpty()) {
                Text("No hay reservas para mostrar")
            }

            // Lista de reservas
            LazyColumn {
                items(reservasVisibles) { reserva ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                reserva.nombreEvento,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cliente: ${reserva.correoCliente}")
                            Text("${reserva.fecha} - ${reserva.horario}")
                            Text("Invitados: ${reserva.invitados}")
                            Text("Abono a validar: ${formatoPrecio(reserva.total / 2)}")
                            Text("Estado del abono: ${reserva.estadoAbono}")

                            Spacer(modifier = Modifier.height(8.dp))

                            // Comprobante que subió el cliente (se lee del archivo guardado en filesDir)
                            if (reserva.comprobante.isNotEmpty()) {
                                val imagen = remember(reserva.comprobante) {
                                    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                                    BitmapFactory.decodeFile(reserva.comprobante, opciones)
                                }
                                if (imagen != null) {
                                    Image(
                                        bitmap = imagen.asImageBitmap(),
                                        contentDescription = "Comprobante de transferencia",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                    )
                                } else {
                                    Text("No se pudo leer el comprobante")
                                }
                            } else {
                                Text("Sin comprobante adjunto")
                            }

                            // Solo se puede validar o rechazar cuando el cliente ya adjuntó el comprobante
                            if (reserva.estadoAbono == "En revisión") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.validarAbono(context, reserva.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Validar abono")
                                    }
                                    Button(
                                        onClick = { viewModel.rechazarAbono(context, reserva.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Rechazar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Botón de filtro: burdeos si es el filtro activo, marrón si no
@Composable
private fun BotonFiltro(
    texto: String,
    filtroActual: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (texto == filtroActual) Burdeos else MarronSuave
        )
    ) {
        Text(texto)
    }
}
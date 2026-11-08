package com.ban.paoliciassweet.ui.screen

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.ui.components.BarraInferior
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.BanqueteriaTheme
import com.ban.paoliciassweet.ui.theme.BlancoHueso
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.viewmodel.AgendaViewmodel

@Composable
fun AgendaScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToReserva: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    agendaViewmodel: AgendaViewmodel = viewModel()
) {
    val reservas = agendaViewmodel.uiState.reserva
    val context = LocalContext.current

    // El precio ya viene calculado y guardado en la reserva (tarifa + productos)
    val precio = reservas?.total ?: 0
    val abono = precio / 2

    // Selector de imágenes de la galería (no necesita permisos)
    val selectorImagen = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            agendaViewmodel.guardarComprobante(context, uri)
        }
    }

    Scaffold(
        topBar = { Header(title = "Mi Agenda") },
        bottomBar = {
            BarraInferior(
                onCatalogoClick = onNavigateToHome,
                onReservasClick = onNavigateToReserva,
                onPerfilClick = onNavigateToPerfil
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "Agenda de eventos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Consulta tus reservas y sus fechas programadas.",
                fontSize = 14.sp,
                color = MarronSuave
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (agendaViewmodel.errorMessage != null) {
                Text(
                    text = agendaViewmodel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = if (reservas == null) Alignment.Center else Alignment.TopCenter
            ) {
                if (reservas == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = Burdeos,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aún no tienes reservas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cuando agendes un evento, aparecerá aquí.",
                            fontSize = 14.sp,
                            color = MarronSuave
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoHueso)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = reservas.nombreEvento,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Burdeos
                            )
                            Text(text = "Fecha: ${reservas.fecha}", fontSize = 15.sp)
                            Text(text = "Horario: ${reservas.horario}", fontSize = 15.sp)
                            Text(text = "Invitados: ${reservas.invitados}", fontSize = 15.sp)
                            Text(text = "Observaciones: ${reservas.observaciones}", fontSize = 15.sp)
                            Text(
                                text = "Precio del evento: ${formatoPrecio(precio)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Abono (50%): ${formatoPrecio(abono)}",
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Estado del abono: ${reservas.estadoAbono}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Burdeos
                            )

                            if (reservas.comprobante.isNotEmpty()) {
                                val imagen = remember(reservas.comprobante) {
                                    val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                                    BitmapFactory.decodeFile(reservas.comprobante, opciones)
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
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    selectorImagen.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                enabled = reservas != null && reservas.estadoAbono != "Pagado 50%",
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (reservas?.comprobante.isNullOrEmpty()) {
                        "Adjuntar comprobante"
                    } else {
                        "Cambiar comprobante"
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendaScreenPreview() {
    BanqueteriaTheme {
        AgendaScreen(onNavigateToHome = {})
    }
}
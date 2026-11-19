package com.ban.paoliciassweet.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.model.EstadoAbono
import com.ban.paoliciassweet.model.ReservaRemota
import com.ban.paoliciassweet.ui.components.BarraInferior
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.BanqueteriaTheme
import com.ban.paoliciassweet.ui.theme.BlancoHueso
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.viewmodel.AgendaViewModel
import com.ban.paoliciassweet.util.Externas
import java.io.File

// Colores del texto "Estado del abono" según el estado (la animación viaja entre ellos)
private val ColorPendiente = Burdeos
private val ColorEnRevision = Color(0xFFB26A00)
private val ColorPagado = Color(0xFF2E7D32)

@Composable
fun AgendaScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToReserva: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    agendaViewModel: AgendaViewModel = viewModel()
) {
    val reservas = agendaViewModel.uiState.reservas
    val context = LocalContext.current

    // Id de la reserva a la que se le está adjuntando el comprobante (sobrevive al girar la pantalla)
    var reservaElegida by rememberSaveable { mutableStateOf<Long?>(null) }
    var uriFoto by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        agendaViewModel.cargarReservas()
    }

    // Mensaje de éxito como Toast
    LaunchedEffect(agendaViewModel.mensaje) {
        agendaViewModel.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            agendaViewModel.mensajeMostrado()
        }
    }

    // RECURSO NATIVO 1: galería (selector del sistema, no necesita permisos)
    val selectorImagen = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val id = reservaElegida
        if (uri != null && id != null) agendaViewModel.subirComprobante(id, uri)
    }

    // RECURSO NATIVO 2: cámara. La foto se guarda en la caché privada de la app (FileProvider)
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val id = reservaElegida
        val uri = uriFoto
        if (exito && id != null && uri != null) agendaViewModel.subirComprobante(id, uri)
    }

    fun abrirCamara(reservaId: Long) {
        val carpeta = File(context.cacheDir, "fotos").apply { mkdirs() }
        val archivo = File(carpeta, "comprobante_$reservaId.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        reservaElegida = reservaId
        uriFoto = uri
        try {
            camara.launch(uri)
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(context, "Este dispositivo no tiene cámara", Toast.LENGTH_SHORT).show()
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

            if (agendaViewModel.cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            agendaViewModel.errorMessage?.let { error ->
                Text(text = error, color = Color(0xFFC62828), fontSize = 14.sp)
                TextButton(onClick = { agendaViewModel.cargarReservas() }) { Text("Reintentar") }
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (reservas.isEmpty() && !agendaViewModel.cargando) {
                // Sin reservas: mensaje centrado
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
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
                }
            } else {
                // Una tarjeta por reserva del cliente
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservas, key = { it.id }) { reserva ->
                        if (reserva.tieneComprobante) {
                            LaunchedEffect(reserva.id, reserva.estadoAbono) {
                                agendaViewModel.cargarImagen(reserva.id)
                            }
                        }
                        TarjetaReserva(
                            reserva = reserva,
                            imagen = agendaViewModel.imagenes[reserva.id],
                            subiendo = agendaViewModel.subiendo == reserva.id,
                            onGaleria = {
                                reservaElegida = reserva.id
                                selectorImagen.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onCamara = { abrirCamara(reserva.id) },
                            tasaDolar = agendaViewModel.tasaDolar
                        )
                    }
                }
            }
        }
    }
}

// Tarjeta de una reserva. Cada una tiene sus botones para adjuntar el comprobante.
@Composable
private fun TarjetaReserva(
    reserva: ReservaRemota,
    imagen: ImageBitmap?,
    subiendo: Boolean,
    onGaleria: () -> Unit,
    onCamara: () -> Unit,
    tasaDolar: Double? = null
) {
    // ANIMACIÓN 1: el color del estado cambia de forma gradual (600 ms) en vez de saltar
    val colorEstado by animateColorAsState(
        targetValue = when (reserva.estadoAbono) {
            EstadoAbono.PAGADO, EstadoAbono.COMPLETADO -> ColorPagado
            EstadoAbono.EN_REVISION -> ColorEnRevision
            else -> ColorPendiente
        },
        animationSpec = tween(durationMillis = 600),
        label = "colorEstado"
    )

    // Solo se puede adjuntar o cambiar el comprobante antes de que la admin lo valide
    val puedeAdjuntar = reserva.estadoAbono == EstadoAbono.PENDIENTE ||
            reserva.estadoAbono == EstadoAbono.EN_REVISION

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
                text = reserva.nombreEvento,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Burdeos
            )
            Text(text = "Fecha: ${reserva.fecha}", fontSize = 15.sp)
            Text(text = "Horario: ${reserva.horario}", fontSize = 15.sp)
            Text(text = "Invitados: ${reserva.invitados}", fontSize = 15.sp)
            if (!reserva.observaciones.isNullOrBlank()) {
                Text(text = "Observaciones: ${reserva.observaciones}", fontSize = 15.sp)
            }
            Text(
                text = "Precio del evento: ${formatoPrecio(reserva.total)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Abono (50%): ${formatoPrecio(reserva.abono)}",
                fontSize = 15.sp
            )
            // API externa (ExchangeRate-API): referencia en dólares con el valor del día
            if (tasaDolar != null) {
                Text(
                    text = "≈ ${Externas.formatoDolar(Externas.aDolares(reserva.total, tasaDolar))} " +
                            "(abono ≈ ${Externas.formatoDolar(Externas.aDolares(reserva.abono, tasaDolar))})",
                    fontSize = 13.sp,
                    color = MarronSuave
                )
            }
            Text(
                text = "Estado del abono: ${reserva.estadoAbono}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorEstado
            )
            reserva.fechaPago?.let {
                Text(text = "Abono validado el $it", fontSize = 13.sp, color = MarronSuave)
            }

            // ANIMACIÓN 2: la imagen del comprobante aparece con fundido y expansión
            AnimatedVisibility(visible = imagen != null) {
                if (imagen != null) {
                    Image(
                        bitmap = imagen,
                        contentDescription = "Comprobante de transferencia",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }

            if (subiendo) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando comprobante...", fontSize = 14.sp)
                }
            } else if (puedeAdjuntar) {
                Text(
                    text = if (reserva.tieneComprobante) "Cambiar comprobante:" else "Adjuntar comprobante:",
                    fontSize = 14.sp,
                    color = MarronSuave
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onCamara, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cámara")
                    }
                    OutlinedButton(onClick = onGaleria, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galería")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendaScreenPreview() {
    BanqueteriaTheme {
        val ejemplo = remember {
            ReservaRemota(
                1, "Cumpleaños", "20/12/2026", "20:00", 25, "Sin nueces", "ana@correo.cl",
                EstadoAbono.EN_REVISION, "1", 80000, 215000, 107500, false, null
            )
        }
        TarjetaReserva(ejemplo, null, false, {}, {})
    }
}

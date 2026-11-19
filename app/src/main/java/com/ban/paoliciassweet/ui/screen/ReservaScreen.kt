package com.ban.paoliciassweet.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.model.Cotizacion
import com.ban.paoliciassweet.ui.components.BarraInferior
import com.ban.paoliciassweet.ui.components.CampoValidado
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.BlancoHueso
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.util.Externas
import com.ban.paoliciassweet.ui.viewmodel.ReservaViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAgenda: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {},
    reservaViewModel: ReservaViewModel = viewModel()
) {
    val uiState = reservaViewModel.uiState
    val context = LocalContext.current

    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarReloj by remember { mutableStateOf(false) }

    // Solo se pueden elegir fechas desde hoy en adelante
    val hoyUtc = remember {
        LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >= hoyUtc
        }
    )
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = true
    )

    LaunchedEffect(Unit) {
        reservaViewModel.cargarProductosElegidos()
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val fecha = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        reservaViewModel.onFechaChanged(fecha)
                    }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (mostrarReloj) {
        AlertDialog(
            onDismissRequest = { mostrarReloj = false },
            confirmButton = {
                TextButton(onClick = {
                    val hora = "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                    reservaViewModel.onHorarioChanged(hora)
                    mostrarReloj = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReloj = false }) { Text("Cancelar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        topBar = { Header(title = "Reservar evento") },
        bottomBar = {
            BarraInferior(
                onCatalogoClick = onNavigateToHome,
                onAgendaClick = onNavigateToAgenda,
                onPerfilClick = onNavigateToPerfil
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Reserva tu evento",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Completa los siguientes datos para solicitar tu reserva.",
                fontSize = 14.sp,
                color = MarronSuave
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Productos de tu evento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (reservaViewModel.productosElegidos.isEmpty()) {
                Text(
                    text = "Aún no has agregado productos. Puedes elegirlos desde el Catálogo.",
                    fontSize = 14.sp,
                    color = MarronSuave
                )
            } else {
                reservaViewModel.productosElegidos.forEach { producto ->
                    Text(text = "• ${producto.nombre}", fontSize = 14.sp)
                }
            }

            CampoValidado(
                value = uiState.nombreEvento,
                onValueChange = { reservaViewModel.onNombreEventoChanged(it) },
                label = "Nombre del evento",
                error = uiState.nombreEventoError
            )

            CampoValidado(
                value = uiState.fecha,
                onValueChange = {},
                label = "Fecha del evento",
                error = uiState.fechaError,
                readOnly = true,
                iconoAccion = {
                    IconButton(onClick = { mostrarCalendario = true }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Elegir fecha"
                        )
                    }
                }
            )

            // API externa (Nager.Date): aviso si la fecha es feriado en Chile
            reservaViewModel.avisoFeriado?.let { aviso ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Text(
                        text = "⚠ $aviso",
                        color = Color(0xFFB26A00),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            CampoValidado(
                value = uiState.horario,
                onValueChange = {},
                label = "Horario",
                error = uiState.horarioError,
                readOnly = true,
                iconoAccion = {
                    IconButton(onClick = { mostrarReloj = true }) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = "Elegir hora"
                        )
                    }
                }
            )

            CampoValidado(
                value = uiState.invitados,
                onValueChange = { reservaViewModel.onInvitadosChanged(it) },
                label = "Cantidad de invitados",
                error = uiState.invitadosError,
                keyboardType = KeyboardType.Number
            )

            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = { reservaViewModel.onObservacionesChanged(it) },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            // Desglose calculado por el backend (tarifa + productos) antes de enviar
            reservaViewModel.cotizacion?.let { TarjetaCotizacion(it, reservaViewModel.tasaDolar) }
            reservaViewModel.cotizacionError?.let {
                Text(text = it, color = MarronSuave, fontSize = 13.sp)
            }

            reservaViewModel.errorMessage?.let {
                Text(text = it, color = Color(0xFFC62828), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            reservaViewModel.successMessage?.let {
                Text(text = it, color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    // Vibra solo cuando el servidor confirmó la reserva
                    reservaViewModel.guardarReserva { vibrar(context, 300L) }
                },
                enabled = !reservaViewModel.cargando,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                if (reservaViewModel.cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(text = "Enviar reserva")
                }
            }
        }
    }
}

@Composable
private fun TarjetaCotizacion(c: Cotizacion, tasaDolar: Double?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlancoHueso),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Cotización para ${c.invitados} invitados", fontWeight = FontWeight.Bold, color = Burdeos)
            FilaPrecio("Servicio de banquetería", c.servicio)
            c.lineas.forEach { l ->
                FilaPrecio("${l.nombre} × ${l.unidades}", l.subtotal)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            FilaPrecio("Total", c.total, negrita = true)
            FilaPrecio("Abono para reservar (50%)", c.abono, negrita = true)
            // API externa (ExchangeRate-API): referencia en dólares con el valor del día
            if (tasaDolar != null) {
                Text(
                    text = "≈ ${Externas.formatoDolar(Externas.aDolares(c.total, tasaDolar))} " +
                            "(1 USD = ${formatoPrecio(Externas.pesosPorDolar(tasaDolar))})",
                    fontSize = 12.sp,
                    color = MarronSuave
                )
                Text("Tipo de cambio: ExchangeRate-API", fontSize = 10.sp, color = MarronSuave)
            }
        }
    }
}

@Composable
private fun FilaPrecio(texto: String, monto: Int, negrita: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val peso = if (negrita) FontWeight.Bold else FontWeight.Normal
        Text(texto, fontSize = 14.sp, fontWeight = peso, modifier = Modifier.weight(1f))
        Text(formatoPrecio(monto), fontSize = 14.sp, fontWeight = peso)
    }
}

package com.ban.paoliciassweet.ui.screen

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.model.CalculoInsumos
import com.ban.paoliciassweet.model.EstadoAbono
import com.ban.paoliciassweet.model.ReservaRemota
import com.ban.paoliciassweet.ui.components.Footer
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoCantidad
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.viewmodel.AdminViewModel
import com.ban.paoliciassweet.util.Notificaciones

private val FILTROS = listOf(
    "Todas", EstadoAbono.PENDIENTE, EstadoAbono.EN_REVISION, EstadoAbono.PAGADO, EstadoAbono.COMPLETADO
)

@Composable
fun PanelAdminScreen(
    onLogout: () -> Unit,
    onVerInsumos: () -> Unit = {},
    viewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val filtroActual = viewModel.uiState.filtro
    val reservasVisibles = viewModel.reservasFiltradas()

    // RECURSO NATIVO: notificaciones. En Android 13+ el permiso se pide en tiempo de ejecución;
    // si la administradora lo niega, el panel funciona igual pero sin avisos.
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            viewModel.cargarReservas() // vuelve a revisar y avisa si hay comprobantes pendientes
        } else {
            Toast.makeText(context, "Sin permiso no recibirás avisos de comprobantes", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarReservas()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Notificaciones.tienePermiso(context)) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.cargarReservas() }, modifier = Modifier.weight(1f)) {
                    Text("Actualizar")
                }
                // Inventario de insumos de la administradora
                Button(onClick = onVerInsumos, modifier = Modifier.weight(1f)) {
                    Text("Insumos")
                }
                // Cerrar sesión (AppNavigation limpia la sesión y el historial)
                OutlinedButton(onClick = onLogout, modifier = Modifier.weight(1f)) {
                    Text("Salir")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filtros por estado del abono (el activo va en burdeos)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(FILTROS) { filtro ->
                    BotonFiltro(filtro, filtroActual) { viewModel.onFiltroChanged(filtro) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            viewModel.errorMessage?.let { error ->
                Text(text = error, color = Color(0xFFC62828), fontSize = 14.sp)
            }

            // Mensaje si no hay reservas para el filtro elegido
            if (reservasVisibles.isEmpty() && !viewModel.cargando) {
                Text("No hay reservas para mostrar", color = MarronSuave)
            }

            // Lista de reservas (ya viene ordenada por fecha desde el backend)
            LazyColumn {
                items(reservasVisibles, key = { it.id }) { reserva ->
                    if (reserva.tieneComprobante) {
                        LaunchedEffect(reserva.id, reserva.estadoAbono) { viewModel.cargarImagen(reserva.id) }
                    }
                    TarjetaAdmin(
                        reserva = reserva,
                        viewModel = viewModel,
                        onValidar = {
                            viewModel.validarAbono(reserva.id) {
                                vibrar(context, 150)
                                Toast.makeText(context, "Abono validado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRechazar = {
                            viewModel.rechazarAbono(reserva.id) {
                                vibrar(context, 400)
                                Toast.makeText(context, "Comprobante rechazado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCompletar = {
                            viewModel.completar(reserva.id) {
                                vibrar(context, 150)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaAdmin(
    reserva: ReservaRemota,
    viewModel: AdminViewModel,
    onValidar: () -> Unit,
    onRechazar: () -> Unit,
    onCompletar: () -> Unit
) {
    val context = LocalContext.current
    val ocupado = viewModel.procesando == reserva.id

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
            // API externa (Nager.Date): aviso si el evento cae en feriado
            viewModel.feriados[reserva.id]?.let { nombre ->
                Text(
                    text = "⚠ Feriado: $nombre (revisar disponibilidad de personal)",
                    color = Color(0xFFB26A00),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text("Invitados: ${reserva.invitados}")
            if (!reserva.observaciones.isNullOrBlank()) {
                Text("Observaciones: ${reserva.observaciones}")
            }
            Text("Total: ${formatoPrecio(reserva.total)}")
            Text("Abono a validar: ${formatoPrecio(reserva.abono)}", fontWeight = FontWeight.Bold)
            Text("Estado del abono: ${reserva.estadoAbono}", color = Burdeos, fontWeight = FontWeight.Bold)
            reserva.fechaPago?.let { Text("Pagado el $it", color = MarronSuave) }

            Spacer(modifier = Modifier.height(8.dp))

            // Comprobante que subió el cliente (se descarga del backend)
            if (reserva.tieneComprobante) {
                val imagen = viewModel.imagenes[reserva.id]
                if (imagen != null) {
                    Image(
                        bitmap = imagen,
                        contentDescription = "Comprobante de transferencia",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                } else {
                    Text("Cargando comprobante...", color = MarronSuave)
                }
            } else {
                Text("Sin comprobante adjunto", color = MarronSuave)
            }

            // Antes de aceptar o rechazar: ¿alcanza el inventario para esta reserva?
            if (reserva.estadoAbono == EstadoAbono.PENDIENTE || reserva.estadoAbono == EstadoAbono.EN_REVISION) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.calcularInsumos(reserva.id) },
                    enabled = viewModel.calculando == null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.calculando == reserva.id) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Calcular insumos")
                    }
                }
                viewModel.calculos[reserva.id]?.let { TarjetaInsumos(it) }
            }

            // Solo se puede validar o rechazar cuando el cliente ya adjuntó el comprobante
            if (reserva.estadoAbono == EstadoAbono.EN_REVISION) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = onValidar, enabled = !ocupado, modifier = Modifier.weight(1f)) {
                        Text("Validar abono")
                    }
                    OutlinedButton(onClick = onRechazar, enabled = !ocupado, modifier = Modifier.weight(1f)) {
                        Text("Rechazar")
                    }
                }
            }

            // Después del evento la administradora la marca como completada
            if (reserva.estadoAbono == EstadoAbono.PAGADO) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onCompletar, enabled = !ocupado, modifier = Modifier.fillMaxWidth()) {
                    Text("Marcar como completada")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Abre la app de correo para contactar al cliente
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${reserva.correoCliente}")
                        putExtra(Intent.EXTRA_SUBJECT, "Tu reserva en Paolicias.Sweet: ${reserva.nombreEvento}")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hola, te escribimos por tu reserva del ${reserva.fecha} a las ${reserva.horario}."
                        )
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No hay app de correo instalada", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contactar cliente por correo")
            }
        }
    }
}

// Resultado de "Calcular insumos": si se puede realizar y qué falta
@Composable
private fun TarjetaInsumos(calculo: CalculoInsumos) {
    val verde = Color(0xFF2E7D32)
    val rojo = Color(0xFFC62828)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = if (calculo.alcanza) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (calculo.alcanza) "✔ Hay insumos suficientes: se puede realizar la reserva"
                else "✖ Faltan insumos para esta reserva",
                fontWeight = FontWeight.Bold,
                color = if (calculo.alcanza) verde else rojo
            )
            if (calculo.insumos.isEmpty()) {
                Text("Esta reserva no lleva productos con receta.", fontSize = 13.sp, color = MarronSuave)
            }
            calculo.insumos.forEach { i ->
                val falta = i.faltante > 0
                Text(
                    text = "${i.nombre}: necesita ${formatoCantidad(i.necesario)} ${i.unidad} · hay ${formatoCantidad(i.stock)}" +
                            if (falta) " · faltan ${formatoCantidad(i.faltante)}" else "",
                    fontSize = 13.sp,
                    color = if (falta) rojo else Color.Unspecified,
                    fontWeight = if (falta) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// Botón de filtro: burdeos si es el filtro activo, marrón si no
@Composable
private fun BotonFiltro(
    texto: String,
    filtroActual: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (texto == filtroActual) Burdeos else MarronSuave
        ),
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Text(texto, fontSize = 13.sp)
    }
}

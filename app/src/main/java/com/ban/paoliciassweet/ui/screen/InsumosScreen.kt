package com.ban.paoliciassweet.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.model.Insumo
import com.ban.paoliciassweet.ui.components.CampoValidado
import com.ban.paoliciassweet.ui.components.Footer
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.BlancoHueso
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoCantidad
import com.ban.paoliciassweet.ui.viewmodel.InsumosViewModel

// Inventario de la administradora: ver insumos y ajustar su stock (por ejemplo, después de comprar)
@Composable
fun InsumosScreen(
    onVolver: () -> Unit,
    viewModel: InsumosViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.cargar() }

    LaunchedEffect(viewModel.mensaje) {
        viewModel.mensaje?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.mensajeMostrado()
        }
    }

    viewModel.editando?.let { insumo ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelarEdicion() },
            title = { Text("Stock de ${insumo.nombre}") },
            text = {
                Column {
                    Text("Ingresa la cantidad que hay ahora (${insumo.unidad}).", fontSize = 14.sp, color = MarronSuave)
                    Spacer(modifier = Modifier.height(8.dp))
                    CampoValidado(
                        value = viewModel.stockTexto,
                        onValueChange = { viewModel.onStockChanged(it) },
                        label = "Stock (${insumo.unidad})",
                        error = viewModel.stockError,
                        keyboardType = KeyboardType.Decimal
                    )
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.guardarStock() }) { Text("Guardar") } },
            dismissButton = { TextButton(onClick = { viewModel.cancelarEdicion() }) { Text("Cancelar") } }
        )
    }

    Scaffold(
        topBar = { Header(title = "INSUMOS") },
        bottomBar = { Footer() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onVolver, modifier = Modifier.weight(1f)) { Text("Volver al panel") }
                Button(onClick = { viewModel.cargar() }, modifier = Modifier.weight(1f)) { Text("Actualizar") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Toca un insumo para ajustar su stock.", fontSize = 13.sp, color = MarronSuave)
            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            viewModel.errorMessage?.let {
                Text(text = it, color = Color(0xFFC62828), fontSize = 14.sp)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.insumos, key = { it.id }) { insumo ->
                    FilaInsumo(insumo) { viewModel.editar(insumo) }
                }
            }
        }
    }
}

@Composable
private fun FilaInsumo(insumo: Insumo, onClick: () -> Unit) {
    // Aviso visual cuando queda poco (menos de 1 kg/L o menos de 10 unidades)
    val poco = if (insumo.unidad == "unidades") insumo.stock < 10 else insumo.stock < 1
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlancoHueso)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(insumo.nombre, fontWeight = FontWeight.Bold, color = Burdeos)
                if (poco) {
                    Text("Queda poco", fontSize = 12.sp, color = Color(0xFFB26A00))
                }
            }
            Text(
                text = "${formatoCantidad(insumo.stock)} ${insumo.unidad}",
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar stock de ${insumo.nombre}",
                tint = MarronSuave,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

package com.ban.paoliciassweet.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.data.local.entity.ProductoEntity
import com.ban.paoliciassweet.ui.components.BarraInferior
import com.ban.paoliciassweet.ui.theme.BanqueteriaTheme
import com.ban.paoliciassweet.ui.theme.BlancoHueso
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.Dorado
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.util.rememberImagenProducto
import com.ban.paoliciassweet.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onProductoClick: (Int) -> Unit,
    onNavigateToReserva: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToPerfil: () -> Unit,
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlancoHueso)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Paolicias.Sweet",
                        fontFamily = FontFamily.Cursive,
                        fontSize = 39.sp
                    )
                    Text(
                        text = "Pastelería & Banquetería",
                        color = Dorado,
                        fontSize = 18.sp
                    )
                }
            }
        },
        bottomBar = {
            BarraInferior(
                onReservasClick = onNavigateToReserva,
                onAgendaClick = onNavigateToAgenda,
                onPerfilClick = onNavigateToPerfil
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = viewModel.uiState.busqueda,
                onValueChange = { viewModel.onBusquedaChanged(it) },
                label = { Text("Buscar productos...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (viewModel.cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Sin conexión: se muestra la copia guardada en Room, con opción de reintentar
            viewModel.aviso?.let { aviso ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = aviso, fontSize = 12.sp, color = MarronSuave, modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.cargarProductos() }) { Text("Reintentar") }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Categorías que salen de la base de datos
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.categorias) { categoria ->
                    BotonCategoria(
                        texto = categoria,
                        seleccionada = viewModel.uiState.categoria == categoria
                    ) { viewModel.onCategoriaChanged(categoria) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val lista = viewModel.productosFiltrados

            if (lista.isEmpty()) {
                Text(
                    text = "No hay productos para mostrar.",
                    color = MarronSuave,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(lista, key = { it.id }) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { onProductoClick(producto.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// Botón redondeado de categoría
@Composable
private fun BotonCategoria(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionada) Burdeos else MarronSuave,
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Dorado),
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Tarjeta de un producto: al tocarla se abre el detalle
@Composable
private fun ProductoCard(
    producto: ProductoEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlancoHueso),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = rememberImagenProducto(producto.imagen)),
                contentDescription = producto.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = producto.nombre, fontWeight = FontWeight.Bold)
                Text(text = producto.descripcion, fontSize = 12.sp, color = MarronSuave)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatoPrecio(producto.precio),
                        color = Burdeos,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (producto.porciones > 1) "Rinde ${producto.porciones} personas" else "Por porción",
                        fontSize = 12.sp,
                        color = MarronSuave
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BanqueteriaTheme {
        HomeScreen(
            onProductoClick = {},
            onNavigateToReserva = {},
            onNavigateToAgenda = {},
            onNavigateToPerfil = {}
        )
    }
}
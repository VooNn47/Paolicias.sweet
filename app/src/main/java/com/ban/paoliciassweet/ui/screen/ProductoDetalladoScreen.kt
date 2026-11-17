package com.ban.paoliciassweet.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.model.SeleccionEvento
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.Dorado
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.util.formatoPrecio
import com.ban.paoliciassweet.ui.util.rememberImagenProducto
import com.ban.paoliciassweet.ui.viewmodel.ProductoDetalleViewModel

@Composable
fun ProductoDetalleScreen(
    productoId: Int,
    onVolver: () -> Unit,
    viewModel: ProductoDetalleViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(productoId) {
        viewModel.cargar(context, productoId)
    }

    val producto = viewModel.producto

    Scaffold(
        topBar = { Header(title = producto?.nombre ?: "Detalle") }
    ) { innerPadding ->

        if (producto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Cargando...", color = MarronSuave)
            }
        } else {
            val agregado = producto.id in SeleccionEvento.productoIds

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painterResource(id = rememberImagenProducto(producto.imagen)),
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = producto.categoria,
                        color = Dorado,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = producto.nombre,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = producto.descripcion, fontSize = 16.sp)

                    Text(
                        text = "Precio por unidad: ${formatoPrecio(producto.precio)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Burdeos
                    )
                    Text(
                        text = if (producto.porciones > 1) {
                            "Cada unidad rinde para ${producto.porciones} personas."
                        } else {
                            "Se cobra por porción individual."
                        },
                        fontSize = 14.sp,
                        color = MarronSuave
                    )

                    Button(
                        onClick = { SeleccionEvento.alternar(producto.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (agregado) MarronSuave else Burdeos
                        )
                    ) {
                        Text(if (agregado) "Quitar de mi evento" else "Agregar a mi evento")
                    }

                    OutlinedButton(
                        onClick = onVolver,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver al catálogo")
                    }
                }
            }
        }
    }
}
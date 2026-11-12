package com.grupo9.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupo9.viewmodel.ProductoViewModel

@Composable
fun ProductoRemotoScreen(viewModel: ProductoViewModel = viewModel()) {

    val productos by viewModel.productos.observeAsState()
    val error by viewModel.error.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarProductos()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        error?.let { Text("Error: $it") }

        LazyColumn {
            items(productos.orEmpty()) { p ->
                Text("${p.nombre} - $${p.precio}")
            }
        }
    }
}
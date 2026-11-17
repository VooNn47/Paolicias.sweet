package com.ban.paoliciassweet.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ban.paoliciassweet.R
import java.text.NumberFormat
import java.util.Locale

// 45000 -> "$45.000"
fun formatoPrecio(monto: Int): String =
    "$" + NumberFormat.getInstance(Locale.forLanguageTag("es-CL")).format(monto)

// Busca el drawable por el nombre guardado en la base. Si no existe, usa "portada".
@Composable
fun rememberImagenProducto(nombre: String): Int {
    val context = LocalContext.current
    return remember(nombre) {
        val id = context.resources.getIdentifier(nombre, "drawable", context.packageName)
        if (id != 0) id else R.drawable.portada
    }
}

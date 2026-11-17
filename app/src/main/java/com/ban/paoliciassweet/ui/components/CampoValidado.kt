package com.ban.paoliciassweet.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun CampoValidado(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false,
    readOnly: Boolean = false,
    iconoAccion: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        readOnly = readOnly,
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(error)
            }
        },
        trailingIcon = {
            if (error != null || iconoAccion != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (error != null) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error en el campo"
                        )
                    }
                    iconoAccion?.invoke()
                }
            }
        },
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth()
    )
}
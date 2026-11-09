package com.ban.paoliciassweet.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.ui.components.CampoValidado
import com.ban.paoliciassweet.ui.components.Footer
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        topBar = { Header(title = "REGISTRO DE USUARIO") },
        bottomBar = { Footer() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Registro", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                CampoValidado(
                    value = state.name,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = "Nombre Completo",
                    error = state.nameError
                )

                Spacer(modifier = Modifier.height(4.dp))

                CampoValidado(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = "Correo Electrónico",
                    error = state.emailError,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(4.dp))

                CampoValidado(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = "Contraseña",
                    error = state.passwordError,
                    keyboardType = KeyboardType.Password,
                    esClave = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                CampoValidado(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                    label = "Repetir Contraseña",
                    error = state.confirmPasswordError,
                    keyboardType = KeyboardType.Password,
                    esClave = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.onRegisterClick(context) {
                            Toast.makeText(
                                context,
                                "¡Registro Exitoso!, Por favor inicia sesión",
                                Toast.LENGTH_SHORT
                            ).show()
                            onNavigateToLogin()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Registrarse")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            IconButton(
                onClick = onNavigateToBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    }
}
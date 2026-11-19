package com.ban.paoliciassweet.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ban.paoliciassweet.R
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.components.BarraInferior
import com.ban.paoliciassweet.ui.components.Header
import com.ban.paoliciassweet.ui.theme.BanqueteriaTheme
import com.ban.paoliciassweet.ui.theme.BlancoHueso
import com.ban.paoliciassweet.ui.theme.Burdeos
import com.ban.paoliciassweet.ui.theme.Crema
import com.ban.paoliciassweet.ui.theme.Dorado
import com.ban.paoliciassweet.ui.theme.MarronSuave
import com.ban.paoliciassweet.ui.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToAgenda: () -> Unit = {},
    onNavigateToReserva: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: PerfilViewModel = viewModel()
) {
    val perfil = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.cargarPerfil(Sesion.correo)
    }

    Scaffold(
        topBar = { Header("Mi Perfil") },
        bottomBar = {
            BarraInferior(
                onCatalogoClick = onNavigateToHome,
                onAgendaClick = onNavigateToAgenda,
                onReservasClick = onNavigateToReserva
            )
        },
        containerColor = Crema
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.tortafrutosrojos),
                contentDescription = "foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .shadow(elevation = 1.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .border(4.dp, Dorado, CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¡Hola, ${perfil.nombre.substringBefore(" ")}!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoHueso)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mis datos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Burdeos
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Burdeos
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Nombre completo",
                                fontSize = 12.sp,
                                color = MarronSuave
                            )
                            Text(text = perfil.nombre, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Email,
                            contentDescription = null,
                            tint = Burdeos
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Correo electrónico",
                                fontSize = 12.sp,
                                color = MarronSuave
                            )
                            Text(text = perfil.correo, fontSize = 15.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cierra la sesión: borra los datos en memoria y vuelve al login
            Button(
                onClick = {
                    onLogout() // AppNavigation cierra la sesión y borra el historial
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    BanqueteriaTheme {
        PerfilScreen()
    }
}
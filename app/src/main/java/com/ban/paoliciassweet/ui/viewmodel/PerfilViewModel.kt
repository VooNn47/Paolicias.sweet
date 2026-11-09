package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.repository.UserRepository
import com.ban.paoliciassweet.model.PerfilModel
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    var uiState by mutableStateOf(PerfilModel())
        private set

    // El ViewModel no habla con Room: pide los datos al Repository
    private fun repositorio(context: Context) =
        UserRepository(AppDatabase.getDatabase(context).userDao())

    fun cargarPerfil(context: Context, email: String) {
        viewModelScope.launch {
            val user = repositorio(context).buscarPorCorreo(email)

            if (user != null) {
                uiState = PerfilModel(
                    nombre = user.name,
                    correo = user.email
                )
            }
        }
    }
}
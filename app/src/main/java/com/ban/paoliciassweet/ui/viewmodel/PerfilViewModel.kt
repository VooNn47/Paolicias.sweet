package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.repository.UserRepository
import com.ban.paoliciassweet.model.PerfilModel
import kotlinx.coroutines.launch

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(PerfilModel())
        private set

    // El ViewModel no habla con Room: pide los datos al Repository
    private val repositorio = UserRepository(AppDatabase.getDatabase(application).userDao())

    fun cargarPerfil(email: String) {
        viewModelScope.launch {
            val user = repositorio.buscarPorCorreo(email)

            if (user != null) {
                uiState = PerfilModel(
                    nombre = user.name,
                    correo = user.email
                )
            }
        }
    }
}

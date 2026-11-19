package com.ban.paoliciassweet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.Roles
import com.ban.paoliciassweet.data.repository.UserRepository
import com.ban.paoliciassweet.model.LoginModel
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.util.validarCorreo
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(LoginModel())
        private set

    // Error general (credenciales incorrectas); los errores por campo van en uiState
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    // El ViewModel no habla con Room: pide los datos al Repository
    private val repositorio = UserRepository(AppDatabase.getDatabase(application).userDao())

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(email = email, emailError = null)
        errorMessage = null
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(password = password, passwordError = null)
        errorMessage = null
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        if (cargando) return
        val state = uiState

        // En el login la contraseña solo debe venir completa; el largo mínimo se exige al registrarse
        val emailError = validarCorreo(state.email)
        val passwordError = if (state.password.isEmpty()) "Ingresa tu contraseña" else null

        if (emailError != null || passwordError != null) {
            uiState = state.copy(emailError = emailError, passwordError = passwordError)
            return
        }

        // trim() quita espacios al inicio y al final; lowercase() pasa todo a minúsculas.
        val email = state.email.trim().lowercase()

        viewModelScope.launch {
            cargando = true
            val user = repositorio.iniciarSesion(email, state.password)
            cargando = false

            if (user != null) {
                errorMessage = null
                Sesion.correo = user.email
                Sesion.nombre = user.name
                // El rol se lee de SQLite (se asignó al registrarse)
                Sesion.esAdmin = user.rol == Roles.ADMIN
                uiState = LoginModel()
                onSuccess()
            } else {
                errorMessage = "Correo o contraseña incorrectos"
            }
        }
    }
}

package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.model.LoginModel
import com.ban.paoliciassweet.model.Sesion
import com.ban.paoliciassweet.ui.util.validarCorreo
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginModel())
        private set

    // Error general (credenciales incorrectas); los errores por campo van en uiState
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChanged(email: String) {
        uiState = uiState.copy(email = email, emailError = null)
        errorMessage = null
    }

    fun onPasswordChanged(password: String) {
        uiState = uiState.copy(password = password, passwordError = null)
        errorMessage = null
    }

    fun onLoginClick(context: Context, onSuccess: () -> Unit) {
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
        val password = state.password

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val user = db.userDao().getUserByEmail(email)

            if (user != null && user.password == password) {
                errorMessage = null
                Sesion.correo = email
                Sesion.esAdmin = (email == "admin@paolicias.cl")
                onSuccess()
            } else {
                errorMessage = "Correo o contraseña incorrectos"
            }
        }
    }
}
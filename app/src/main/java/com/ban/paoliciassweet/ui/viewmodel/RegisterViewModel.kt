package com.ban.paoliciassweet.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ban.paoliciassweet.data.local.database.AppDatabase
import com.ban.paoliciassweet.data.local.entity.UserEntity
import com.ban.paoliciassweet.model.RegisterModel
import com.ban.paoliciassweet.ui.util.validarClave
import com.ban.paoliciassweet.ui.util.validarCorreo
import com.ban.paoliciassweet.ui.util.validarNombre
import com.ban.paoliciassweet.ui.util.validarRepetirClave
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel(){
    var uiState by mutableStateOf(RegisterModel())
        private set

    // Al escribir en un campo se borra solo el error de ese campo
    fun onNameChanged(name: String){
        uiState = uiState.copy(name = name, nameError = null)
    }

    fun onEmailChanged(email: String){
        uiState = uiState.copy(email = email, emailError = null)
    }

    fun onPasswordChanged(password: String){
        uiState = uiState.copy(password = password, passwordError = null)
    }

    fun onConfirmPasswordChanged(confirmPassword: String){
        uiState = uiState.copy(confirmPassword = confirmPassword, confirmPasswordError = null)
    }

    fun onRegisterClick(context: Context, onSuccess: () -> Unit){
        val state = uiState

        // Se validan todos los campos a la vez para mostrar todos los errores juntos
        val nameError = validarNombre(state.name)
        val emailError = validarCorreo(state.email)
        val passwordError = validarClave(state.password)
        val confirmError = validarRepetirClave(state.password, state.confirmPassword)

        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            uiState = state.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmError
            )
            return
        }

        // El correo se guarda sin espacios y en minúsculas, igual que en el login
        val correo = state.email.trim().lowercase()

        viewModelScope.launch {
            val db = AppDatabase.getDatabase(context)
            val existingUser = db.userDao().getUserByEmail(correo)

            if(existingUser != null) {
                uiState = uiState.copy(emailError = "Este correo ya está registrado")
            }else {
                val newUser = UserEntity(
                    name = state.name.trim(),
                    email = correo,
                    password = state.password
                )
                db.userDao().registerUser(newUser)
                onSuccess()
            }
        }
    }
}
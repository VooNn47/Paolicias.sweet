package com.ban.paoliciassweet.model

data class LoginModel(
    val email: String = "",
    val password: String = "",
    // Un mensaje de error por campo (null = el campo está bien)
    val emailError: String? = null,
    val passwordError: String? = null
)
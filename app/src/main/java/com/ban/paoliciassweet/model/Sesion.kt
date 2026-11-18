package com.ban.paoliciassweet.model

// Usuario con sesión iniciada (vive en memoria; al cerrar la app hay que volver a entrar)
object Sesion {
    var correo: String = ""
    var nombre: String = ""
    var esAdmin: Boolean = false

    fun cerrar() {
        correo = ""
        nombre = ""
        esAdmin = false
        SeleccionEvento.limpiar()
    }
}

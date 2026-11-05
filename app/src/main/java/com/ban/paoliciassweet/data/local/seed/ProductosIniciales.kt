package com.ban.paoliciassweet.data.local.seed


import com.ban.paoliciassweet.data.local.entity.ProductoEntity

// Productos que se insertan la primera vez que la tabla está vacía.
// "porciones" = a cuántas personas rinde UNA unidad. Los precios son de ejemplo.
val productosIniciales = listOf(
    // Tortas
    ProductoEntity(
        nombre = "Torta Frutos Rojos",
        descripcion = "Torta de bizcocho de vainilla con crema y frutos rojos.",
        categoria = "Tortas", precio = 45000, porciones = 12, imagen = "tortafrutosrojos"
    ),
    ProductoEntity(
        nombre = "Torta Tres Leches",
        descripcion = "Bizcocho esponjoso bañado en tres leches con merengue.",
        categoria = "Tortas", precio = 32000, porciones = 12, imagen = "tortatresleches"
    ),
    ProductoEntity(
        nombre = "Torta de Chocolate",
        descripcion = "Bizcocho de chocolate húmedo con ganache semi amargo.",
        categoria = "Tortas", precio = 40000, porciones = 12, imagen = "tortachocolate"
    ),
    // Pasteles
    ProductoEntity(
        nombre = "Kuchen de Frambuesa",
        descripcion = "Masa quebrada con crema pastelera y frambuesas.",
        categoria = "Pasteles", precio = 22000, porciones = 8, imagen = "kuchenframbuesa"
    ),
    ProductoEntity(
        nombre = "Pie de Limón",
        descripcion = "Base de galleta, crema de limón y merengue tostado.",
        categoria = "Pasteles", precio = 20000, porciones = 8, imagen = "pielimon"
    ),
    // Postres
    ProductoEntity(
        nombre = "Cheesecake Arándanos",
        descripcion = "Cheesecake cremoso con coulis de arándanos.",
        categoria = "Postres", precio = 38000, porciones = 10, imagen = "cheskake"
    ),
    ProductoEntity(
        nombre = "Tiramisú en Vaso",
        descripcion = "Postre individual de café, mascarpone y cacao.",
        categoria = "Postres", precio = 2800, porciones = 1, imagen = "tiramisu"
    ),
    ProductoEntity(
        nombre = "Mousse de Maracuyá",
        descripcion = "Postre individual suave y ácido de maracuyá.",
        categoria = "Postres", precio = 2500, porciones = 1, imagen = "moussemaracuya"
    ),
    // Platos de fondo
    ProductoEntity(
        nombre = "Lasaña de Carne",
        descripcion = "Lasaña con salsa boloñesa, bechamel y queso gratinado.",
        categoria = "Platos de fondo", precio = 6500, porciones = 1, imagen = "lasana"
    ),
    ProductoEntity(
        nombre = "Pollo al Champiñón",
        descripcion = "Pechuga de pollo en salsa cremosa de champiñones.",
        categoria = "Platos de fondo", precio = 6900, porciones = 1, imagen = "pollochampinon"
    ),
    ProductoEntity(
        nombre = "Pastel de Choclo",
        descripcion = "Pino de carne, pollo y huevo bajo una capa de choclo.",
        categoria = "Platos de fondo", precio = 5900, porciones = 1, imagen = "pastelchoclo"
    )
)
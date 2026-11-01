package com.example.conexin_bd_ferreteria.data.model

import java.util.Locale

data class Producto(
    val codigoProducto: Int = 0,
    val fabricante: String,
    val valorProducto: Double
) {
    val codigoFormatted: String
        get() = String.format(Locale.ROOT, "PRO%06d", codigoProducto)
}

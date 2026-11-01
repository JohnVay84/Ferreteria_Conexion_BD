package com.example.conexin_bd_ferreteria.data.model

import java.util.Locale

data class Cliente(
    val codigoCliente: Int = 0,
    val nombre: String,
    val direccion: String,
    val telefono: String
) {
    val codigoFormatted: String
        get() = String.format(Locale.ROOT, "CLI%06d", codigoCliente)
}

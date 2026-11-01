package com.example.conexin_bd_ferreteria.data.model

import java.util.Locale

data class Pedido(
    val codigoPedido: Int = 0,
    val descripcion: String,
    val fecha: String,
    val clienteId: Int,
    val nombreCliente: String = ""
) {
    val codigoFormatted: String
        get() = String.format(Locale.ROOT, "PED%06d", codigoPedido)

    val codigoClienteFormatted: String
        get() = String.format(Locale.ROOT, "CLI%06d", clienteId)
}

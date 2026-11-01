package com.example.conexin_bd_ferreteria.data.model

import java.util.Locale

data class DetalleFactura(
    val codigoFactura: Int = 0,
    val codigoProducto: Int,
    val fabricanteProducto: String = "",
    val cantidad: Int,
    val precioUnitario: Double
) {
    val codigoProductoFormatted: String
        get() = String.format(Locale.ROOT, "PRO%06d", codigoProducto)

    val subtotal: Double
        get() = cantidad * precioUnitario
}

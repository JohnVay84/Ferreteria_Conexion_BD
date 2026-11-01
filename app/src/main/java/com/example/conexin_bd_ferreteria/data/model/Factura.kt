package com.example.conexin_bd_ferreteria.data.model

import java.util.Locale

data class Factura(
    val codigoFactura: Int = 0,
    val fecha: String,
    val valorFactura: Double,
    val pedidoId: Int? = null,
    val clienteNombre: String = "",
    val clienteCodigoFormatted: String = "",
    val detalles: List<DetalleFactura> = emptyList()
) {
    val codigoFormatted: String
        get() = String.format(Locale.ROOT, "FAC%06d", codigoFactura)

    val codigoPedidoFormatted: String?
        get() = pedidoId?.let { String.format(Locale.ROOT, "PED%06d", it) }
}

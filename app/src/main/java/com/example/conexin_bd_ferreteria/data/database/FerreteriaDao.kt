package com.example.conexin_bd_ferreteria.data.database

import android.content.ContentValues
import android.content.Context
import com.example.conexin_bd_ferreteria.data.model.Cliente
import com.example.conexin_bd_ferreteria.data.model.DetalleFactura
import com.example.conexin_bd_ferreteria.data.model.Factura
import com.example.conexin_bd_ferreteria.data.model.Pedido
import com.example.conexin_bd_ferreteria.data.model.Producto
import java.util.Locale

class FerreteriaDao(context: Context) {

    private val dbHelper = FerreteriaDatabaseHelper(context)

    // ==================== OPERACIONES CLIENTE ====================

    fun insertCliente(cliente: Cliente): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FerreteriaDatabaseHelper.COL_CLIENTE_NOMBRE, cliente.nombre)
            put(FerreteriaDatabaseHelper.COL_CLIENTE_DIRECCION, cliente.direccion)
            put(FerreteriaDatabaseHelper.COL_CLIENTE_TELEFONO, cliente.telefono)
        }
        return db.insert(FerreteriaDatabaseHelper.TABLE_CLIENTE, null, values)
    }

    fun updateCliente(cliente: Cliente): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FerreteriaDatabaseHelper.COL_CLIENTE_NOMBRE, cliente.nombre)
            put(FerreteriaDatabaseHelper.COL_CLIENTE_DIRECCION, cliente.direccion)
            put(FerreteriaDatabaseHelper.COL_CLIENTE_TELEFONO, cliente.telefono)
        }
        return db.update(
            FerreteriaDatabaseHelper.TABLE_CLIENTE,
            values,
            "${FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO} = ?",
            arrayOf(cliente.codigoCliente.toString())
        )
    }

    fun deleteCliente(codigoCliente: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            FerreteriaDatabaseHelper.TABLE_CLIENTE,
            "${FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO} = ?",
            arrayOf(codigoCliente.toString())
        )
    }

    fun getAllClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            FerreteriaDatabaseHelper.TABLE_CLIENTE,
            null, null, null, null, null,
            "${FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO} ASC"
        )

        cursor.use { c ->
            val idxCodigo = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO)
            val idxNombre = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_CLIENTE_NOMBRE)
            val idxDireccion = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_CLIENTE_DIRECCION)
            val idxTelefono = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_CLIENTE_TELEFONO)

            while (c.moveToNext()) {
                clientes.add(
                    Cliente(
                        codigoCliente = c.getInt(idxCodigo),
                        nombre = c.getString(idxNombre),
                        direccion = c.getString(idxDireccion),
                        telefono = c.getString(idxTelefono)
                    )
                )
            }
        }
        return clientes
    }

    // ==================== OPERACIONES PRODUCTO ====================

    fun insertProducto(producto: Producto): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FerreteriaDatabaseHelper.COL_PRODUCTO_FABRICANTE, producto.fabricante)
            put(FerreteriaDatabaseHelper.COL_PRODUCTO_VALOR, producto.valorProducto)
        }
        return db.insert(FerreteriaDatabaseHelper.TABLE_PRODUCTO, null, values)
    }

    fun updateProducto(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FerreteriaDatabaseHelper.COL_PRODUCTO_FABRICANTE, producto.fabricante)
            put(FerreteriaDatabaseHelper.COL_PRODUCTO_VALOR, producto.valorProducto)
        }
        return db.update(
            FerreteriaDatabaseHelper.TABLE_PRODUCTO,
            values,
            "${FerreteriaDatabaseHelper.COL_PRODUCTO_CODIGO} = ?",
            arrayOf(producto.codigoProducto.toString())
        )
    }

    fun deleteProducto(codigoProducto: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            FerreteriaDatabaseHelper.TABLE_PRODUCTO,
            "${FerreteriaDatabaseHelper.COL_PRODUCTO_CODIGO} = ?",
            arrayOf(codigoProducto.toString())
        )
    }

    fun getAllProductos(): List<Producto> {
        val productos = mutableListOf<Producto>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            FerreteriaDatabaseHelper.TABLE_PRODUCTO,
            null, null, null, null, null,
            "${FerreteriaDatabaseHelper.COL_PRODUCTO_CODIGO} ASC"
        )

        cursor.use { c ->
            val idxCodigo = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PRODUCTO_CODIGO)
            val idxFabricante = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PRODUCTO_FABRICANTE)
            val idxValor = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PRODUCTO_VALOR)

            while (c.moveToNext()) {
                productos.add(
                    Producto(
                        codigoProducto = c.getInt(idxCodigo),
                        fabricante = c.getString(idxFabricante),
                        valorProducto = c.getDouble(idxValor)
                    )
                )
            }
        }
        return productos
    }

    // ==================== OPERACIONES PEDIDO ====================

    fun insertPedido(pedido: Pedido): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FerreteriaDatabaseHelper.COL_PEDIDO_DESCRIPCION, pedido.descripcion)
            put(FerreteriaDatabaseHelper.COL_PEDIDO_FECHA, pedido.fecha)
            put(FerreteriaDatabaseHelper.COL_PEDIDO_CLIENTE_ID, pedido.clienteId)
        }
        return db.insert(FerreteriaDatabaseHelper.TABLE_PEDIDO, null, values)
    }

    fun updatePedido(pedido: Pedido): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FerreteriaDatabaseHelper.COL_PEDIDO_DESCRIPCION, pedido.descripcion)
            put(FerreteriaDatabaseHelper.COL_PEDIDO_FECHA, pedido.fecha)
            put(FerreteriaDatabaseHelper.COL_PEDIDO_CLIENTE_ID, pedido.clienteId)
        }
        return db.update(
            FerreteriaDatabaseHelper.TABLE_PEDIDO,
            values,
            "${FerreteriaDatabaseHelper.COL_PEDIDO_CODIGO} = ?",
            arrayOf(pedido.codigoPedido.toString())
        )
    }

    fun deletePedido(codigoPedido: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            FerreteriaDatabaseHelper.TABLE_PEDIDO,
            "${FerreteriaDatabaseHelper.COL_PEDIDO_CODIGO} = ?",
            arrayOf(codigoPedido.toString())
        )
    }

    fun getAllPedidos(): List<Pedido> {
        val pedidos = mutableListOf<Pedido>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT p.${FerreteriaDatabaseHelper.COL_PEDIDO_CODIGO},
                   p.${FerreteriaDatabaseHelper.COL_PEDIDO_DESCRIPCION},
                   p.${FerreteriaDatabaseHelper.COL_PEDIDO_FECHA},
                   p.${FerreteriaDatabaseHelper.COL_PEDIDO_CLIENTE_ID},
                   c.${FerreteriaDatabaseHelper.COL_CLIENTE_NOMBRE} AS nombre_cliente
            FROM ${FerreteriaDatabaseHelper.TABLE_PEDIDO} p
            INNER JOIN ${FerreteriaDatabaseHelper.TABLE_CLIENTE} c
            ON p.${FerreteriaDatabaseHelper.COL_PEDIDO_CLIENTE_ID} = c.${FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO}
            ORDER BY p.${FerreteriaDatabaseHelper.COL_PEDIDO_CODIGO} ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use { c ->
            val idxCodigo = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PEDIDO_CODIGO)
            val idxDesc = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PEDIDO_DESCRIPCION)
            val idxFecha = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PEDIDO_FECHA)
            val idxClienteId = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_PEDIDO_CLIENTE_ID)
            val idxNombreCliente = c.getColumnIndexOrThrow("nombre_cliente")

            while (c.moveToNext()) {
                pedidos.add(
                    Pedido(
                        codigoPedido = c.getInt(idxCodigo),
                        descripcion = c.getString(idxDesc),
                        fecha = c.getString(idxFecha),
                        clienteId = c.getInt(idxClienteId),
                        nombreCliente = c.getString(idxNombreCliente)
                    )
                )
            }
        }
        return pedidos
    }

    // ==================== OPERACIONES FACTURA ====================

    fun insertFacturaConDetalles(
        fecha: String,
        pedidoId: Int?,
        detalles: List<Pair<Int, Int>>
    ): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        var facturaId: Long = -1

        try {
            var totalFactura = 0.0
            val productosMap = getAllProductos().associateBy { it.codigoProducto }

            detalles.forEach { (prodId, cant) ->
                val prod = productosMap[prodId]
                if (prod != null) {
                    totalFactura += prod.valorProducto * cant
                }
            }

            val valuesFactura = ContentValues().apply {
                put(FerreteriaDatabaseHelper.COL_FACTURA_FECHA, fecha)
                put(FerreteriaDatabaseHelper.COL_FACTURA_VALOR, totalFactura)
                if (pedidoId != null) {
                    put(FerreteriaDatabaseHelper.COL_FACTURA_PEDIDO_ID, pedidoId)
                } else {
                    putNull(FerreteriaDatabaseHelper.COL_FACTURA_PEDIDO_ID)
                }
            }

            facturaId = db.insert(FerreteriaDatabaseHelper.TABLE_FACTURA, null, valuesFactura)

            if (facturaId != -1L) {
                detalles.forEach { (prodId, cant) ->
                    val prod = productosMap[prodId]
                    if (prod != null) {
                        val valuesDetalle = ContentValues().apply {
                            put(FerreteriaDatabaseHelper.COL_DETALLE_FACTURA_ID, facturaId)
                            put(FerreteriaDatabaseHelper.COL_DETALLE_PRODUCTO_ID, prodId)
                            put(FerreteriaDatabaseHelper.COL_DETALLE_CANTIDAD, cant)
                            put(FerreteriaDatabaseHelper.COL_DETALLE_PRECIO_UNITARIO, prod.valorProducto)
                        }
                        db.insert(FerreteriaDatabaseHelper.TABLE_DETALLE_FACTURA, null, valuesDetalle)
                    }
                }
                db.setTransactionSuccessful()
            }
        } finally {
            db.endTransaction()
        }
        return facturaId
    }

    fun deleteFactura(codigoFactura: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            FerreteriaDatabaseHelper.TABLE_FACTURA,
            "${FerreteriaDatabaseHelper.COL_FACTURA_CODIGO} = ?",
            arrayOf(codigoFactura.toString())
        )
    }

    fun getAllFacturas(): List<Factura> {
        val facturas = mutableListOf<Factura>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT f.${FerreteriaDatabaseHelper.COL_FACTURA_CODIGO},
                   f.${FerreteriaDatabaseHelper.COL_FACTURA_FECHA},
                   f.${FerreteriaDatabaseHelper.COL_FACTURA_VALOR},
                   f.${FerreteriaDatabaseHelper.COL_FACTURA_PEDIDO_ID},
                   c.${FerreteriaDatabaseHelper.COL_CLIENTE_NOMBRE} AS nombre_cliente,
                   c.${FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO} AS cliente_id
            FROM ${FerreteriaDatabaseHelper.TABLE_FACTURA} f
            LEFT JOIN ${FerreteriaDatabaseHelper.TABLE_PEDIDO} p ON f.${FerreteriaDatabaseHelper.COL_FACTURA_PEDIDO_ID} = p.${FerreteriaDatabaseHelper.COL_PEDIDO_CODIGO}
            LEFT JOIN ${FerreteriaDatabaseHelper.TABLE_CLIENTE} c ON p.${FerreteriaDatabaseHelper.COL_PEDIDO_CLIENTE_ID} = c.${FerreteriaDatabaseHelper.COL_CLIENTE_CODIGO}
            ORDER BY f.${FerreteriaDatabaseHelper.COL_FACTURA_CODIGO} DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use { c ->
            val idxCodigo = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_FACTURA_CODIGO)
            val idxFecha = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_FACTURA_FECHA)
            val idxValor = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_FACTURA_VALOR)
            val idxPedidoId = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_FACTURA_PEDIDO_ID)
            val idxNombreCliente = c.getColumnIndexOrThrow("nombre_cliente")
            val idxClienteId = c.getColumnIndexOrThrow("cliente_id")

            while (c.moveToNext()) {
                val fId = c.getInt(idxCodigo)
                val pId = if (c.isNull(idxPedidoId)) null else c.getInt(idxPedidoId)
                val clienteNombre = if (c.isNull(idxNombreCliente)) "Venta Directa" else c.getString(idxNombreCliente)
                val clienteId = if (c.isNull(idxClienteId)) 0 else c.getInt(idxClienteId)
                val clienteCodigoFormatted = if (clienteId > 0) String.format(Locale.ROOT, "CLI%06d", clienteId) else "N/A"

                val detalles = getDetallesForFactura(fId)

                facturas.add(
                    Factura(
                        codigoFactura = fId,
                        fecha = c.getString(idxFecha),
                        valorFactura = c.getDouble(idxValor),
                        pedidoId = pId,
                        clienteNombre = clienteNombre,
                        clienteCodigoFormatted = clienteCodigoFormatted,
                        detalles = detalles
                    )
                )
            }
        }
        return facturas
    }

    private fun getDetallesForFactura(codigoFactura: Int): List<DetalleFactura> {
        val detalles = mutableListOf<DetalleFactura>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT d.${FerreteriaDatabaseHelper.COL_DETALLE_FACTURA_ID},
                   d.${FerreteriaDatabaseHelper.COL_DETALLE_PRODUCTO_ID},
                   d.${FerreteriaDatabaseHelper.COL_DETALLE_CANTIDAD},
                   d.${FerreteriaDatabaseHelper.COL_DETALLE_PRECIO_UNITARIO},
                   p.${FerreteriaDatabaseHelper.COL_PRODUCTO_FABRICANTE} AS fabricante
            FROM ${FerreteriaDatabaseHelper.TABLE_DETALLE_FACTURA} d
            INNER JOIN ${FerreteriaDatabaseHelper.TABLE_PRODUCTO} p
            ON d.${FerreteriaDatabaseHelper.COL_DETALLE_PRODUCTO_ID} = p.${FerreteriaDatabaseHelper.COL_PRODUCTO_CODIGO}
            WHERE d.${FerreteriaDatabaseHelper.COL_DETALLE_FACTURA_ID} = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(codigoFactura.toString()))

        cursor.use { c ->
            val idxFacturaId = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_DETALLE_FACTURA_ID)
            val idxProductoId = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_DETALLE_PRODUCTO_ID)
            val idxCantidad = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_DETALLE_CANTIDAD)
            val idxPrecio = c.getColumnIndexOrThrow(FerreteriaDatabaseHelper.COL_DETALLE_PRECIO_UNITARIO)
            val idxFabricante = c.getColumnIndexOrThrow("fabricante")

            while (c.moveToNext()) {
                detalles.add(
                    DetalleFactura(
                        codigoFactura = c.getInt(idxFacturaId),
                        codigoProducto = c.getInt(idxProductoId),
                        fabricanteProducto = c.getString(idxFabricante),
                        cantidad = c.getInt(idxCantidad),
                        precioUnitario = c.getDouble(idxPrecio)
                    )
                )
            }
        }
        return detalles
    }

    // ==================== POBLAR 10 REGISTROS POR ENTIDAD ====================

    fun seedInitialDataIfEmpty() {
        val currentClientes = getAllClientes()
        if (currentClientes.size < 10) {
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM ${FerreteriaDatabaseHelper.TABLE_DETALLE_FACTURA}")
            db.execSQL("DELETE FROM ${FerreteriaDatabaseHelper.TABLE_FACTURA}")
            db.execSQL("DELETE FROM ${FerreteriaDatabaseHelper.TABLE_PEDIDO}")
            db.execSQL("DELETE FROM ${FerreteriaDatabaseHelper.TABLE_PRODUCTO}")
            db.execSQL("DELETE FROM ${FerreteriaDatabaseHelper.TABLE_CLIENTE}")
            db.execSQL("DELETE FROM sqlite_sequence")

            // 10 CLIENTES (CLI000001 a CLI000010)
            val c1 = insertCliente(Cliente(nombre = "Carlos Rodríguez", direccion = "Av. Las Luces #452", telefono = "3001234567"))
            val c2 = insertCliente(Cliente(nombre = "María Fernanda Gómez", direccion = "Calle 10 #12-34", telefono = "3159876543"))
            val c3 = insertCliente(Cliente(nombre = "Construcciones El Roble S.A.", direccion = "Zona Industrial Lote 5", telefono = "6044445566"))
            val c4 = insertCliente(Cliente(nombre = "Juan Pablo Martínez", direccion = "Carrera 45 #88-12", telefono = "3105551234"))
            val c5 = insertCliente(Cliente(nombre = "Ferretería & Taller Los Andes", direccion = "Av. Bolívar #102", telefono = "3187779988"))
            val c6 = insertCliente(Cliente(nombre = "Andrés Felipe Torres", direccion = "Diagonal 15 #30-40", telefono = "3124443322"))
            val c7 = insertCliente(Cliente(nombre = "Industrias Metalmecánicas S.A.S.", direccion = "Parque Industrial Sur", telefono = "6013332211"))
            val c8 = insertCliente(Cliente(nombre = "Lucía Esperanza Morales", direccion = "Calle 70 #4-15", telefono = "3016667788"))
            val c9 = insertCliente(Cliente(nombre = "Taller de Tornería Don Pedro", direccion = "Av. Del Ferrocarril #55", telefono = "3162228899"))
            val c10 = insertCliente(Cliente(nombre = "Roberto Antonio Vargas", direccion = "Transversal 22 #18-09", telefono = "3148881122"))

            // 10 PRODUCTOS (PRO000001 a PRO000010)
            val p1 = insertProducto(Producto(fabricante = "Rodamiento SKF - 6204-2RSH", valorProducto = 28500.0))
            val p2 = insertProducto(Producto(fabricante = "Fierro Corrugado 1/2 in x 6m - Aceros Arequipa", valorProducto = 45000.0))
            val p3 = insertProducto(Producto(fabricante = "Rodamiento Timken - 30206 Tapered Roller", valorProducto = 62000.0))
            val p4 = insertProducto(Producto(fabricante = "Platina de Fierro 1/4 x 2 in x 6m", valorProducto = 89000.0))
            val p5 = insertProducto(Producto(fabricante = "Rodamiento NSK - 6309-ZZ C3", valorProducto = 75000.0))
            val p6 = insertProducto(Producto(fabricante = "Ángulo de Fierro Estructural 2 x 2 x 1/4 in", valorProducto = 115000.0))
            val p7 = insertProducto(Producto(fabricante = "Rodamiento FAG Rodillos Cilíndricos NU205", valorProducto = 98000.0))
            val p8 = insertProducto(Producto(fabricante = "Barra Cuadrada de Fierro 5/8 in x 6m", valorProducto = 58000.0))
            val p9 = insertProducto(Producto(fabricante = "Soporte Chumacera UCP 205 (Rodamiento)", valorProducto = 42000.0))
            val p10 = insertProducto(Producto(fabricante = "Tubo Estructural de Fierro 3x3 in Cal 16", valorProducto = 135000.0))

            // 10 PEDIDOS (PED000001 a PED000010)
            val ped1 = insertPedido(Pedido(descripcion = "Suministro de rodamientos de precisión SKF", fecha = "2026-10-15", clienteId = c1.toInt()))
            val ped2 = insertPedido(Pedido(descripcion = "Lote de fierro corrugado para cimentación de obra", fecha = "2026-10-16", clienteId = c3.toInt()))
            val ped3 = insertPedido(Pedido(descripcion = "Rodamientos cónicos Timken y chumaceras para torno", fecha = "2026-10-18", clienteId = c9.toInt()))
            val ped4 = insertPedido(Pedido(descripcion = "Platinas y ángulos de fierro estructural para rejas", fecha = "2026-10-20", clienteId = c5.toInt()))
            val ped5 = insertPedido(Pedido(descripcion = "Repuestos de rodamientos FAG e NSK industriales", fecha = "2026-10-21", clienteId = c7.toInt()))
            insertPedido(Pedido(descripcion = "Tubos estructurales y barras cuadradas de fierro", fecha = "2026-10-22", clienteId = c2.toInt()))
            insertPedido(Pedido(descripcion = "Rodamientos blindados y soportes UCP 205", fecha = "2026-10-23", clienteId = c4.toInt()))
            insertPedido(Pedido(descripcion = "Fierro corrugado 1/2 in y barras de fierro", fecha = "2026-10-24", clienteId = c6.toInt()))
            insertPedido(Pedido(descripcion = "Mantenimiento urgente con rodamientos SKF y NSK", fecha = "2026-10-25", clienteId = c8.toInt()))
            insertPedido(Pedido(descripcion = "Estructura metálica pesada con tubos y ángulos de fierro", fecha = "2026-10-26", clienteId = c10.toInt()))

            // FACTURAS (FAC000001, FAC000002, FAC000003, FAC000004, FAC000005) CON DETALLES DE COMPRA
            insertFacturaConDetalles(
                fecha = "2026-10-15",
                pedidoId = ped1.toInt(),
                detalles = listOf(
                    Pair(p1.toInt(), 4), // 4x Rodamientos SKF
                    Pair(p4.toInt(), 2)  // 2x Platinas de Fierro
                )
            )

            insertFacturaConDetalles(
                fecha = "2026-10-16",
                pedidoId = ped2.toInt(),
                detalles = listOf(
                    Pair(p2.toInt(), 10), // 10x Fierro Corrugado
                    Pair(p6.toInt(), 5)   // 5x Ángulos de Fierro
                )
            )

            insertFacturaConDetalles(
                fecha = "2026-10-18",
                pedidoId = ped3.toInt(),
                detalles = listOf(
                    Pair(p3.toInt(), 6), // 6x Rodamientos Timken
                    Pair(p9.toInt(), 4)  // 4x Chumaceras UCP 205
                )
            )

            insertFacturaConDetalles(
                fecha = "2026-10-20",
                pedidoId = ped4.toInt(),
                detalles = listOf(
                    Pair(p4.toInt(), 5), // 5x Platinas
                    Pair(p8.toInt(), 8)  // 8x Barras Cuadradas
                )
            )

            insertFacturaConDetalles(
                fecha = "2026-10-21",
                pedidoId = ped5.toInt(),
                detalles = listOf(
                    Pair(p5.toInt(), 3), // 3x Rodamientos NSK
                    Pair(p7.toInt(), 2), // 2x Rodamientos FAG
                    Pair(p10.toInt(), 4) // 4x Tubos Estructurales
                )
            )
        }
    }
}

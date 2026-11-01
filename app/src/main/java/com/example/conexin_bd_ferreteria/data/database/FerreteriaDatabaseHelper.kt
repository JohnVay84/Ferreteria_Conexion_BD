package com.example.conexin_bd_ferreteria.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * PASO 1: Descripción del proceso para realizar la conexión entre el lenguaje de programación (Java/Kotlin)
 * y el motor de bases de datos SQLite utilizando la clase SQLiteOpenHelper.
 *
 * La clase SQLiteOpenHelper proporciona las utilidades necesarias para abrir, crear, actualizar
 * y gestionar la base de datos de manera eficiente e integrada en la plataforma Android.
 *
 * PASO 2: Se crea una clase que extiende (hereda) de SQLiteOpenHelper.
 */
class FerreteriaDatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ferreteria_don_john.db"
        const val DATABASE_VERSION = 1

        // Tabla Cliente
        const val TABLE_CLIENTE = "Cliente"
        const val COL_CLIENTE_CODIGO = "codigo_cliente"
        const val COL_CLIENTE_NOMBRE = "nombre"
        const val COL_CLIENTE_DIRECCION = "direccion"
        const val COL_CLIENTE_TELEFONO = "telefono"

        // Tabla Pedido
        const val TABLE_PEDIDO = "Pedido"
        const val COL_PEDIDO_CODIGO = "codigo_pedido"
        const val COL_PEDIDO_DESCRIPCION = "descripcion"
        const val COL_PEDIDO_FECHA = "fecha"
        const val COL_PEDIDO_CLIENTE_ID = "cliente_id"

        // Tabla Producto
        const val TABLE_PRODUCTO = "Producto"
        const val COL_PRODUCTO_CODIGO = "codigo_producto"
        const val COL_PRODUCTO_FABRICANTE = "fabricante"
        const val COL_PRODUCTO_VALOR = "valor_producto"

        // Tabla Factura
        const val TABLE_FACTURA = "Factura"
        const val COL_FACTURA_CODIGO = "codigo_factura"
        const val COL_FACTURA_FECHA = "fecha"
        const val COL_FACTURA_VALOR = "valor_factura"
        const val COL_FACTURA_PEDIDO_ID = "pedido_id"

        // Tabla Detalle_Factura (Resultado del proceso de Normalización hasta la 3FN)
        const val TABLE_DETALLE_FACTURA = "Detalle_Factura"
        const val COL_DETALLE_FACTURA_ID = "codigo_factura"
        const val COL_DETALLE_PRODUCTO_ID = "codigo_producto"
        const val COL_DETALLE_CANTIDAD = "cantidad"
        const val COL_DETALLE_PRECIO_UNITARIO = "precio_unitario"
    }

    /**
     * PASO 5: El constructor de la clase.
     * En Kotlin se especifica en la firma del constructor primario y delega al superconstructor
     * de SQLiteOpenHelper recibiendo el Context, Nombre de BD, CursorFactory y Versión.
     */

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        // Habilitar la verificación de claves foráneas
        db?.setForeignKeyConstraintsEnabled(true)
    }

    /**
     * PASO 3: Sobreescribir el método onCreate().
     * Se invoca automáticamente cuando la base de datos se crea por primera vez.
     * En este método se ejecutan las sentencias SQL (DDL) para crear todas las tablas
     * definidas en el modelo Entidad-Relación normalizado hasta la 3FN.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        // 1. Sentencia para crear la tabla Cliente
        val createClienteTable = """
            CREATE TABLE $TABLE_CLIENTE (
                $COL_CLIENTE_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CLIENTE_NOMBRE TEXT NOT NULL,
                $COL_CLIENTE_DIRECCION TEXT NOT NULL,
                $COL_CLIENTE_TELEFONO TEXT NOT NULL
            );
        """.trimIndent()

        // 2. Sentencia para crear la tabla Pedido (Relación 1 a N con Cliente)
        val createPedidoTable = """
            CREATE TABLE $TABLE_PEDIDO (
                $COL_PEDIDO_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PEDIDO_DESCRIPCION TEXT NOT NULL,
                $COL_PEDIDO_FECHA TEXT NOT NULL,
                $COL_PEDIDO_CLIENTE_ID INTEGER NOT NULL,
                FOREIGN KEY ($COL_PEDIDO_CLIENTE_ID) REFERENCES $TABLE_CLIENTE($COL_CLIENTE_CODIGO) ON DELETE CASCADE
            );
        """.trimIndent()

        // 3. Sentencia para crear la tabla Producto
        val createProductoTable = """
            CREATE TABLE $TABLE_PRODUCTO (
                $COL_PRODUCTO_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRODUCTO_FABRICANTE TEXT NOT NULL,
                $COL_PRODUCTO_VALOR REAL NOT NULL
            );
        """.trimIndent()

        // 4. Sentencia para crear la tabla Factura
        val createFacturaTable = """
            CREATE TABLE $TABLE_FACTURA (
                $COL_FACTURA_CODIGO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FACTURA_FECHA TEXT NOT NULL,
                $COL_FACTURA_VALOR REAL NOT NULL,
                $COL_FACTURA_PEDIDO_ID INTEGER,
                FOREIGN KEY ($COL_FACTURA_PEDIDO_ID) REFERENCES $TABLE_PEDIDO($COL_PEDIDO_CODIGO) ON DELETE SET NULL
            );
        """.trimIndent()

        // 5. Sentencia para crear la tabla Detalle_Factura (Normalización 3FN - Clave Compuesta)
        val createDetalleFacturaTable = """
            CREATE TABLE $TABLE_DETALLE_FACTURA (
                $COL_DETALLE_FACTURA_ID INTEGER NOT NULL,
                $COL_DETALLE_PRODUCTO_ID INTEGER NOT NULL,
                $COL_DETALLE_CANTIDAD INTEGER NOT NULL,
                $COL_DETALLE_PRECIO_UNITARIO REAL NOT NULL,
                PRIMARY KEY ($COL_DETALLE_FACTURA_ID, $COL_DETALLE_PRODUCTO_ID),
                FOREIGN KEY ($COL_DETALLE_FACTURA_ID) REFERENCES $TABLE_FACTURA($COL_FACTURA_CODIGO) ON DELETE CASCADE,
                FOREIGN KEY ($COL_DETALLE_PRODUCTO_ID) REFERENCES $TABLE_PRODUCTO($COL_PRODUCTO_CODIGO) ON DELETE RESTRICT
            );
        """.trimIndent()

        db?.execSQL(createClienteTable)
        db?.execSQL(createPedidoTable)
        db?.execSQL(createProductoTable)
        db?.execSQL(createFacturaTable)
        db?.execSQL(createDetalleFacturaTable)
    }

    /**
     * PASO 4: Sobreescribir el método onUpgrade().
     * Se ejecuta automáticamente cuando se incrementa la constante DATABASE_VERSION.
     * Permite gestionar la migración o recreación del esquema de la base de datos.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DETALLE_FACTURA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FACTURA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTO")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PEDIDO")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTE")
        onCreate(db)
    }
}

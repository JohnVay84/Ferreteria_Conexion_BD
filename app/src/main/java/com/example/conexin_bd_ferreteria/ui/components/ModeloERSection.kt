package com.example.conexin_bd_ferreteria.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModeloERSection() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Modelo Entidad-Relación, Normalización 3FN y SQLiteOpenHelper",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "Ferretería Rodamientos y Fierros - Don John",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        HorizontalDivider()

        // SECCIÓN 1: REQUERIMIENTOS Y MODELO E-R
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Requerimientos del Modelo Entidad-Relación",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "• CLIENTE: código, nombre, dirección, teléfono.")
                Text(text = "• PEDIDO: código del pedido, descripción, fecha. (Un cliente puede hacer varios pedidos; un pedido pertenece a un solo cliente: Relación 1 a N).")
                Text(text = "• PRODUCTO: código del producto, fabricante, valor del producto.")
                Text(text = "• FACTURA: código de la factura, fecha, valor de la factura, pedido asociado y detalle de productos.")
            }
        }

        // SECCIÓN 2: NORMALIZACIÓN A 3FN
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "2. Normalización del Modelo hasta la 3ª Forma Normal (3FN)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "A. Primera Forma Normal (1FN):",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Se eliminan los grupos repetitivos o atributos multivaluados. Para registrar múltiples productos en una factura o pedido sin repetir datos en la entidad principal, se descompone la relación N:M creando la entidad intermedia 'Detalle_Factura'.")

                Text(
                    text = "B. Segunda Forma Normal (2FN):",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Estando en 1FN, se elimina cualquier dependencia parcial. En la tabla 'Detalle_Factura', los campos 'cantidad' y 'precio_unitario' dependen de la clave primaria compuesta (codigo_factura, codigo_producto). Los datos propios del producto (fabricante, valor_base) quedan aislados en la entidad 'Producto'.")

                Text(
                    text = "C. Tercera Forma Normal (3FN):",
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Estando en 2FN, se eliminan dependencias transitivas. Ningún atributo no clave depende de otro atributo no clave. Por ejemplo, en 'Pedido' el código de cliente es clave foránea (FK), evitando duplicar nombre o dirección de cliente en la tabla pedido.")
            }
        }

        // SECCIÓN 3: ESTRUCTURA DE TABLAS EN SQLITE
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. Esquema SQL Resultante (Tablas Normalizadas en 3FN)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                CodeSnippet(
                    code = """
-- Tabla 1: Cliente
CREATE TABLE Cliente (
    codigo_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    direccion TEXT NOT NULL,
    telefono TEXT NOT NULL
);

-- Tabla 2: Pedido (Relación 1:N con Cliente)
CREATE TABLE Pedido (
    codigo_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
    descripcion TEXT NOT NULL,
    fecha TEXT NOT NULL,
    cliente_id INTEGER NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(codigo_cliente) ON DELETE CASCADE
);

-- Tabla 3: Producto
CREATE TABLE Producto (
    codigo_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    fabricante TEXT NOT NULL,
    valor_producto REAL NOT NULL
);

-- Tabla 4: Factura
CREATE TABLE Factura (
    codigo_factura INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    valor_factura REAL NOT NULL,
    pedido_id INTEGER,
    FOREIGN KEY (pedido_id) REFERENCES Pedido(codigo_pedido) ON DELETE SET NULL
);

-- Tabla 5: Detalle_Factura (Entidad Intermedia 3FN)
CREATE TABLE Detalle_Factura (
    codigo_factura INTEGER NOT NULL,
    codigo_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario REAL NOT NULL,
    PRIMARY KEY (codigo_factura, codigo_producto),
    FOREIGN KEY (codigo_factura) REFERENCES Factura(codigo_factura) ON DELETE CASCADE,
    FOREIGN KEY (codigo_producto) REFERENCES Producto(codigo_producto) ON DELETE RESTRICT
);
                    """.trimIndent()
                )
            }
        }

        // SECCIÓN 4: PASO A PASO SQLITEOPENHELPER
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "4. Paso a Paso: Conexión SQLite con SQLiteOpenHelper",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Paso 1: Descripción de la Conexión",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "Para conectar una aplicación Android con el motor SQLite, el framework provee la clase abstracta SQLiteOpenHelper, que se encarga de gestionar la creación, apertura y actualización de versiones de la base de datos de manera eficiente.")

                Text(
                    text = "Paso 2: Crear una clase que extienda de SQLiteOpenHelper",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "Definimos una clase personalizada (ej: FerreteriaDatabaseHelper) que hereda de SQLiteOpenHelper.")

                CodeSnippet(
                    code = """
// En Java:
public class FerreteriaDatabaseHelper extends SQLiteOpenHelper { ... }

// En Kotlin:
class FerreteriaDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) { ... }
                    """.trimIndent()
                )

                Text(
                    text = "Paso 3: Sobreescribir el método onCreate()",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "En onCreate() ejecutamos las instrucciones DDL (CREATE TABLE) para construir las tablas de la BD la primera vez que se abre la aplicación.")

                CodeSnippet(
                    code = """
@Override
public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE Cliente (...)");
    db.execSQL("CREATE TABLE Pedido (...)");
    db.execSQL("CREATE TABLE Producto (...)");
    db.execSQL("CREATE TABLE Factura (...)");
    db.execSQL("CREATE TABLE Detalle_Factura (...)");
}
                    """.trimIndent()
                )

                Text(
                    text = "Paso 4: Sobreescribir el método onUpgrade()",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "Se ejecuta cuando se incrementa la versión de la base de datos (DATABASE_VERSION), permitiendo modificar la estructura de las tablas mediante ALTER TABLE o DROP TABLE y recreación.")

                CodeSnippet(
                    code = """
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS Detalle_Factura");
    db.execSQL("DROP TABLE IF EXISTS Factura");
    db.execSQL("DROP TABLE IF EXISTS Producto");
    db.execSQL("DROP TABLE IF EXISTS Pedido");
    db.execSQL("DROP TABLE IF EXISTS Cliente");
    onCreate(db);
}
                    """.trimIndent()
                )

                Text(
                    text = "Paso 5: Crear el constructor de la clase",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(text = "El constructor recibe el Contexto de la aplicación, el Nombre de la base de datos ('ferreteria_don_john.db') y la Versión (ej: 1), invocando al superconstructor super(context, name, factory, version).")

                CodeSnippet(
                    code = """
public FerreteriaDatabaseHelper(Context context) {
    super(context, "ferreteria_don_john.db", null, 1);
}
                    """.trimIndent()
                )
            }
        }
    }
}

@Composable
fun CodeSnippet(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = code,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFF00FF66)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ModeloERSectionPreview() {
    com.example.conexin_bd_ferreteria.ui.theme.Conexión_BD_FerreteriaTheme {
        ModeloERSection()
    }
}

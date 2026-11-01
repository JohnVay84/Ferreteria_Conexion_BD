package com.example.conexin_bd_ferreteria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.conexin_bd_ferreteria.data.model.Producto
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductoSection(
    productos: List<Producto>,
    onAddProducto: (String, Double) -> Unit,
    onUpdateProducto: (Producto) -> Unit,
    onDeleteProducto: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // Search mode: 0 = Por Código, 1 = Por Producto
    var searchMode by remember { mutableStateOf(0) }

    var showAddDialog by remember { mutableStateOf(false) }
    var productoDetailSelected by remember { mutableStateOf<Producto?>(null) }
    var productoToEdit by remember { mutableStateOf<Producto?>(null) }

    val filteredProductos = remember(productos, searchQuery, searchMode) {
        if (searchQuery.isBlank()) {
            productos
        } else {
            productos.filter {
                if (searchMode == 0) {
                    it.codigoFormatted.contains(searchQuery, ignoreCase = true) ||
                            it.codigoProducto.toString().contains(searchQuery)
                } else {
                    it.fabricante.contains(searchQuery, ignoreCase = true) ||
                            it.valorProducto.toString().contains(searchQuery)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar Producto") },
                text = { Text("Nuevo Producto") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Módulo de Productos (Rodamientos y Fierros)",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // OPCIONES DE BÚSQUEDA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Criterio de búsqueda:", style = MaterialTheme.typography.labelLarge)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = searchMode == 0,
                                onClick = { searchMode = 0 }
                            )
                            Text(text = "Código Producto")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = searchMode == 1,
                                onClick = { searchMode = 1 }
                            )
                            Text(text = "Producto")
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = {
                            Text(if (searchMode == 0) "Ejemplo: PRO000001" else "Ejemplo: Rodamiento SKF o Fierro Corrugado")
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ENCABEZADO DE LA GRILLA DE PRODUCTOS
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CÓDIGO",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.width(100.dp)
                    )
                    Text(
                        text = "PRODUCTO",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ACCIÓN",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // LISTADO DE GRILLA DE PRODUCTOS
            if (filteredProductos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (productos.isEmpty()) "No hay productos en SQLite." else "Sin resultados de búsqueda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredProductos, key = { it.codigoProducto }) { producto ->
                        ProductoGridRow(
                            producto = producto,
                            onVerDetalle = { productoDetailSelected = producto }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DETALLE DEL PRODUCTO
    if (productoDetailSelected != null) {
        val current = productoDetailSelected!!
        val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")) }

        AlertDialog(
            onDismissRequest = { productoDetailSelected = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detalles del Producto")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Código: ${current.codigoFormatted}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Text(text = "Producto:", style = MaterialTheme.typography.labelMedium)
                    Text(text = current.fabricante, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Valor Unitario: ${currencyFormat.format(current.valorProducto)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        productoToEdit = current
                        productoDetailSelected = null
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar")
                    }

                    Button(
                        onClick = {
                            onDeleteProducto(current.codigoProducto)
                            productoDetailSelected = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { productoDetailSelected = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showAddDialog) {
        ProductoDialog(
            title = "Agregar Nuevo Producto",
            initialFabricante = "",
            initialValor = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { fabricante, valor ->
                onAddProducto(fabricante, valor)
                showAddDialog = false
            }
        )
    }

    if (productoToEdit != null) {
        val current = productoToEdit!!
        ProductoDialog(
            title = "Editar Producto [${current.codigoFormatted}]",
            initialFabricante = current.fabricante,
            initialValor = current.valorProducto.toString(),
            onDismiss = { productoToEdit = null },
            onConfirm = { fabricante, valor ->
                onUpdateProducto(current.copy(fabricante = fabricante, valorProducto = valor))
                productoToEdit = null
            }
        )
    }
}

@Composable
fun ProductoGridRow(
    producto: Producto,
    onVerDetalle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = producto.codigoFormatted,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = producto.fabricante,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onVerDetalle,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ver Detalle")
            }
        }
    }
}

@Composable
fun ProductoDialog(
    title: String,
    initialFabricante: String,
    initialValor: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var fabricante by remember { mutableStateOf(initialFabricante) }
    var valorText by remember { mutableStateOf(initialValor) }

    val valorDouble = valorText.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fabricante,
                    onValueChange = { fabricante = it },
                    label = { Text("Nombre / Descripción del Producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = valorText,
                    onValueChange = { valorText = it },
                    label = { Text("Valor ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(fabricante, valorDouble) },
                enabled = fabricante.isNotBlank() && valorDouble > 0
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

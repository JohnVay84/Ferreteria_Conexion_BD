package com.example.conexin_bd_ferreteria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.conexin_bd_ferreteria.data.model.Factura
import com.example.conexin_bd_ferreteria.data.model.Pedido
import com.example.conexin_bd_ferreteria.data.model.Producto
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FacturaSection(
    facturas: List<Factura>,
    pedidos: List<Pedido>,
    productos: List<Producto>,
    onAddFactura: (String, Int?, List<Pair<Int, Int>>) -> Unit,
    onDeleteFactura: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // Search mode: 0 = Por Código, 1 = Por Cliente, 2 = Por Rango de Fecha
    var searchMode by remember { mutableStateOf(0) }

    var fechaDesde by remember { mutableStateOf("2026-10-01") }
    var fechaHasta by remember { mutableStateOf("2026-10-31") }

    var showAddDialog by remember { mutableStateOf(false) }
    var facturaDetailSelected by remember { mutableStateOf<Factura?>(null) }

    val filteredFacturas = remember(facturas, searchQuery, searchMode, fechaDesde, fechaHasta) {
        when (searchMode) {
            0 -> {
                if (searchQuery.isBlank()) facturas
                else facturas.filter {
                    it.codigoFormatted.contains(searchQuery, ignoreCase = true) ||
                            it.codigoFactura.toString().contains(searchQuery)
                }
            }
            1 -> {
                if (searchQuery.isBlank()) facturas
                else facturas.filter {
                    it.clienteNombre.contains(searchQuery, ignoreCase = true) ||
                            it.clienteCodigoFormatted.contains(searchQuery, ignoreCase = true) ||
                            (it.codigoPedidoFormatted?.contains(searchQuery, ignoreCase = true) == true)
                }
            }
            2 -> {
                facturas.filter { f ->
                    val fFecha = f.fecha.trim()
                    val fDesde = fechaDesde.trim()
                    val fHasta = fechaHasta.trim()

                    val matchDesde = if (fDesde.isNotBlank()) fFecha >= fDesde else true
                    val matchHasta = if (fHasta.isNotBlank()) fFecha <= fHasta else true
                    matchDesde && matchHasta
                }
            }
            else -> facturas
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Generar Factura") },
                text = { Text("Nueva Factura") }
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
                text = "Módulo de Facturas",
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

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = searchMode == 0,
                                    onClick = { searchMode = 0 }
                                )
                                Text(text = "Código")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = searchMode == 1,
                                    onClick = { searchMode = 1 }
                                )
                                Text(text = "Cliente")
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = searchMode == 2,
                                    onClick = { searchMode = 2 }
                                )
                                Text(text = "Rango de Fecha")
                            }
                        }
                    }

                    if (searchMode == 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = fechaDesde,
                                onValueChange = { fechaDesde = it },
                                label = { Text("Fecha Desde") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = fechaHasta,
                                onValueChange = { fechaHasta = it },
                                label = { Text("Fecha Hasta") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = {
                                Text(if (searchMode == 0) "Ejemplo: FAC000001" else "Ejemplo: Carlos Rodríguez")
                            },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // ENCABEZADO DE LA GRILLA DE FACTURAS
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
                        text = "CLIENTE / MONTO TOTAL",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ACCIÓN",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // LISTADO DE GRILLA DE FACTURAS
            if (filteredFacturas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (facturas.isEmpty()) "No hay facturas en SQLite." else "Sin resultados de búsqueda para el rango o criterio seleccionado.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredFacturas, key = { it.codigoFactura }) { factura ->
                        FacturaGridRow(
                            factura = factura,
                            onVerDetalle = { facturaDetailSelected = factura }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DETALLE DE LA FACTURA
    if (facturaDetailSelected != null) {
        val current = facturaDetailSelected!!
        val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")) }

        AlertDialog(
            onDismissRequest = { facturaDetailSelected = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comprobante de Factura")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Factura: ${current.codigoFormatted}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Fecha: ${current.fecha}" + (current.codigoPedidoFormatted?.let { " | Pedido: $it" } ?: " | Venta Directa"),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(text = "Cliente Facturado:", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${current.clienteNombre} [${current.clienteCodigoFormatted}]",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    Text(text = "Productos Comprados (Detalle):", style = MaterialTheme.typography.labelMedium)

                    current.detalles.forEach { det ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "[${det.codigoProductoFormatted}] ${det.fabricanteProducto}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Cant: ${det.cantidad} x ${currencyFormat.format(det.precioUnitario)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Text(
                                text = currencyFormat.format(det.subtotal),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "VALOR TOTAL: ${currencyFormat.format(current.valorFactura)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteFactura(current.codigoFactura)
                        facturaDetailSelected = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar Factura")
                }
            },
            dismissButton = {
                TextButton(onClick = { facturaDetailSelected = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showAddDialog) {
        FacturaDialog(
            pedidos = pedidos,
            productos = productos,
            onDismiss = { showAddDialog = false },
            onConfirm = { fecha, pedidoId, detalles ->
                onAddFactura(fecha, pedidoId, detalles)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun FacturaGridRow(
    factura: Factura,
    onVerDetalle: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")) }

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
                    text = factura.codigoFormatted,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${factura.clienteNombre} [${factura.clienteCodigoFormatted}]",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = "Fecha: ${factura.fecha} | Total: ${currencyFormat.format(factura.valorFactura)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacturaDialog(
    pedidos: List<Pedido>,
    productos: List<Producto>,
    onDismiss: () -> Unit,
    onConfirm: (String, Int?, List<Pair<Int, Int>>) -> Unit
) {
    val currentDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var fecha by remember { mutableStateOf(currentDate) }

    var selectedPedido by remember { mutableStateOf<Pedido?>(null) }
    var pedidoDropdownExpanded by remember { mutableStateOf(false) }

    val lineItems = remember { mutableStateListOf<Pair<Producto, Int>>() }

    var selectedProductoToAdd by remember { mutableStateOf<Producto?>(productos.firstOrNull()) }
    var productoDropdownExpanded by remember { mutableStateOf(false) }
    var cantidadText by remember { mutableStateOf("1") }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")) }

    val totalCalculado = remember(lineItems.toList()) {
        lineItems.sumOf { it.first.valorProducto * it.second }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Generar Factura de Venta") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha de Factura (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Asociar Pedido (Opcional):", style = MaterialTheme.typography.labelLarge)
                ExposedDropdownMenuBox(
                    expanded = pedidoDropdownExpanded,
                    onExpandedChange = { pedidoDropdownExpanded = !pedidoDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPedido?.let { "[${it.codigoFormatted}] - ${it.nombreCliente}" } ?: "Sin Pedido Asociado (Venta Directa)",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                    )

                    ExposedDropdownMenu(
                        expanded = pedidoDropdownExpanded,
                        onDismissRequest = { pedidoDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ninguno (Venta Directa)") },
                            onClick = {
                                selectedPedido = null
                                pedidoDropdownExpanded = false
                            }
                        )
                        pedidos.forEach { ped ->
                            DropdownMenuItem(
                                text = { Text("[${ped.codigoFormatted}] ${ped.nombreCliente} (${ped.descripcion})") },
                                onClick = {
                                    selectedPedido = ped
                                    pedidoDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider()

                Text(text = "Agregar Productos a la Factura:", style = MaterialTheme.typography.labelLarge)

                ExposedDropdownMenuBox(
                    expanded = productoDropdownExpanded,
                    onExpandedChange = { productoDropdownExpanded = !productoDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedProductoToAdd?.let { "[${it.codigoFormatted}] ${it.fabricante}" } ?: "Seleccione Producto",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                    )

                    ExposedDropdownMenu(
                        expanded = productoDropdownExpanded,
                        onDismissRequest = { productoDropdownExpanded = false }
                    ) {
                        productos.forEach { prod ->
                            DropdownMenuItem(
                                text = { Text("[${prod.codigoFormatted}] ${prod.fabricante} - ${currencyFormat.format(prod.valorProducto)}") },
                                onClick = {
                                    selectedProductoToAdd = prod
                                    productoDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = cantidadText,
                        onValueChange = { cantidadText = it },
                        label = { Text("Cantidad") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val cant = cantidadText.toIntOrNull() ?: 1
                            val prod = selectedProductoToAdd
                            if (prod != null && cant > 0) {
                                lineItems.add(Pair(prod, cant))
                            }
                        },
                        enabled = selectedProductoToAdd != null && (cantidadText.toIntOrNull() ?: 0) > 0
                    ) {
                        Text("Agregar")
                    }
                }

                if (lineItems.isNotEmpty()) {
                    Text(text = "Ítems a Facturar:", style = MaterialTheme.typography.labelMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        lineItems.forEachIndexed { idx, pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "[${pair.first.codigoFormatted}] ${pair.first.fabricante} x${pair.second}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = currencyFormat.format(pair.first.valorProducto * pair.second),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { lineItems.removeAt(idx) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar item", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    Text(
                        text = "VALOR TOTAL FACTURA: ${currencyFormat.format(totalCalculado)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val detalles = lineItems.map { Pair(it.first.codigoProducto, it.second) }
                    onConfirm(fecha, selectedPedido?.codigoPedido, detalles)
                },
                enabled = lineItems.isNotEmpty() && fecha.isNotBlank()
            ) {
                Text("Guardar Factura")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

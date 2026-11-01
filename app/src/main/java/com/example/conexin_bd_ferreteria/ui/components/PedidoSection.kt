package com.example.conexin_bd_ferreteria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.conexin_bd_ferreteria.data.model.Cliente
import com.example.conexin_bd_ferreteria.data.model.Pedido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PedidoSection(
    pedidos: List<Pedido>,
    clientes: List<Cliente>,
    onAddPedido: (String, String, Int) -> Unit,
    onDeletePedido: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // Search mode: 0 = Por Código, 1 = Por Cliente/Descripción
    var searchMode by remember { mutableStateOf(0) }

    var showAddDialog by remember { mutableStateOf(false) }
    var pedidoDetailSelected by remember { mutableStateOf<Pedido?>(null) }

    val filteredPedidos = remember(pedidos, searchQuery, searchMode) {
        if (searchQuery.isBlank()) {
            pedidos
        } else {
            pedidos.filter {
                if (searchMode == 0) {
                    it.codigoFormatted.contains(searchQuery, ignoreCase = true) ||
                            it.codigoPedido.toString().contains(searchQuery)
                } else {
                    it.nombreCliente.contains(searchQuery, ignoreCase = true) ||
                            it.codigoClienteFormatted.contains(searchQuery, ignoreCase = true) ||
                            it.descripcion.contains(searchQuery, ignoreCase = true) ||
                            it.fecha.contains(searchQuery)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo Pedido") },
                text = { Text("Nuevo Pedido") }
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
                text = "Módulo de Pedidos",
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
                            Text(text = "Código Pedido")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = searchMode == 1,
                                onClick = { searchMode = 1 }
                            )
                            Text(text = "Cliente / Descripción")
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = {
                            Text(if (searchMode == 0) "Ejemplo: PED000001" else "Ejemplo: Carlos o Suministro")
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ENCABEZADO DE LA GRILLA DE PEDIDOS
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
                        text = "DESCRIPCIÓN / CLIENTE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ACCIÓN",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // LISTADO DE GRILLA DE PEDIDOS
            if (filteredPedidos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (pedidos.isEmpty()) "No hay pedidos en SQLite." else "Sin resultados de búsqueda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredPedidos, key = { it.codigoPedido }) { pedido ->
                        PedidoGridRow(
                            pedido = pedido,
                            onVerDetalle = { pedidoDetailSelected = pedido }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DETALLE DEL PEDIDO
    if (pedidoDetailSelected != null) {
        val current = pedidoDetailSelected!!

        AlertDialog(
            onDismissRequest = { pedidoDetailSelected = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detalles del Pedido")
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

                    Text(text = "Fecha de registro: ${current.fecha}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Cliente Solicitante:", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${current.nombreCliente} [${current.codigoClienteFormatted}]",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Descripción del Pedido:", style = MaterialTheme.typography.labelMedium)
                    Text(text = current.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePedido(current.codigoPedido)
                        pedidoDetailSelected = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar Pedido")
                }
            },
            dismissButton = {
                TextButton(onClick = { pedidoDetailSelected = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showAddDialog) {
        PedidoDialog(
            clientes = clientes,
            onDismiss = { showAddDialog = false },
            onConfirm = { descripcion, fecha, clienteId ->
                onAddPedido(descripcion, fecha, clienteId)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PedidoGridRow(
    pedido: Pedido,
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
                    text = pedido.codigoFormatted,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pedido.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = "Cliente: ${pedido.nombreCliente} [${pedido.codigoClienteFormatted}]",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
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
fun PedidoDialog(
    clientes: List<Cliente>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    val currentDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var fecha by remember { mutableStateOf(currentDate) }

    var selectedCliente by remember { mutableStateOf<Cliente?>(clientes.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Registrar Nuevo Pedido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Seleccionar Cliente:", style = MaterialTheme.typography.labelLarge)

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCliente?.let { "${it.nombre} [${it.codigoFormatted}]" } ?: "Seleccione un cliente",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        if (clientes.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay clientes creados. Primero agregue un cliente.") },
                                onClick = { dropdownExpanded = false }
                            )
                        } else {
                            clientes.forEach { cliente ->
                                DropdownMenuItem(
                                    text = { Text("[${cliente.codigoFormatted}] ${cliente.nombre}") },
                                    onClick = {
                                        selectedCliente = cliente
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del Pedido") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedCliente?.let {
                        onConfirm(descripcion, fecha, it.codigoCliente)
                    }
                },
                enabled = selectedCliente != null && descripcion.isNotBlank() && fecha.isNotBlank()
            ) {
                Text("Crear Pedido")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

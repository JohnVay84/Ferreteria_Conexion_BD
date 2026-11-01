package com.example.conexin_bd_ferreteria.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.conexin_bd_ferreteria.data.model.Cliente

@Composable
fun ClienteSection(
    clientes: List<Cliente>,
    onAddCliente: (String, String, String) -> Unit,
    onUpdateCliente: (Cliente) -> Unit,
    onDeleteCliente: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // Search mode: 0 = Por Código, 1 = Por Nombre
    var searchMode by remember { mutableStateOf(0) }

    var showAddDialog by remember { mutableStateOf(false) }
    var clienteDetailSelected by remember { mutableStateOf<Cliente?>(null) }
    var clienteToEdit by remember { mutableStateOf<Cliente?>(null) }

    val filteredClientes = remember(clientes, searchQuery, searchMode) {
        if (searchQuery.isBlank()) {
            clientes
        } else {
            clientes.filter {
                if (searchMode == 0) {
                    it.codigoFormatted.contains(searchQuery, ignoreCase = true) ||
                            it.codigoCliente.toString().contains(searchQuery)
                } else {
                    it.nombre.contains(searchQuery, ignoreCase = true) ||
                            it.direccion.contains(searchQuery, ignoreCase = true) ||
                            it.telefono.contains(searchQuery)
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar Cliente") },
                text = { Text("Nuevo Cliente") }
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
                text = "Módulo de Clientes",
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
                            Text(text = "Código Cliente")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = searchMode == 1,
                                onClick = { searchMode = 1 }
                            )
                            Text(text = "Nombre Cliente")
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = {
                            Text(if (searchMode == 0) "Ejemplo: CLI000001" else "Ejemplo: Carlos Rodríguez")
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ENCABEZADO DE LA GRILLA DE CLIENTES
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
                        text = "NOMBRE DEL CLIENTE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ACCIÓN",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // LISTADO DE GRILLA DE CLIENTES
            if (filteredClientes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (clientes.isEmpty()) "No hay clientes en SQLite." else "Sin resultados de búsqueda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredClientes, key = { it.codigoCliente }) { cliente ->
                        ClienteGridRow(
                            cliente = cliente,
                            onVerDetalle = { clienteDetailSelected = cliente }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DETALLE DEL CLIENTE
    if (clienteDetailSelected != null) {
        val current = clienteDetailSelected!!
        AlertDialog(
            onDismissRequest = { clienteDetailSelected = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detalles del Cliente")
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

                    Text(text = "Nombre: ${current.nombre}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                    Text(text = "Dirección: ${current.direccion}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Teléfono: ${current.telefono}", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        clienteToEdit = current
                        clienteDetailSelected = null
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Editar")
                    }

                    Button(
                        onClick = {
                            onDeleteCliente(current.codigoCliente)
                            clienteDetailSelected = null
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
                TextButton(onClick = { clienteDetailSelected = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showAddDialog) {
        ClienteDialog(
            title = "Agregar Nuevo Cliente",
            initialNombre = "",
            initialDireccion = "",
            initialTelefono = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, direccion, telefono ->
                onAddCliente(nombre, direccion, telefono)
                showAddDialog = false
            }
        )
    }

    if (clienteToEdit != null) {
        val current = clienteToEdit!!
        ClienteDialog(
            title = "Editar Cliente [${current.codigoFormatted}]",
            initialNombre = current.nombre,
            initialDireccion = current.direccion,
            initialTelefono = current.telefono,
            onDismiss = { clienteToEdit = null },
            onConfirm = { nombre, direccion, telefono ->
                onUpdateCliente(current.copy(nombre = nombre, direccion = direccion, telefono = telefono))
                clienteToEdit = null
            }
        )
    }
}

@Composable
fun ClienteGridRow(
    cliente: Cliente,
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
                    text = cliente.codigoFormatted,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = cliente.nombre,
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
fun ClienteDialog(
    title: String,
    initialNombre: String,
    initialDireccion: String,
    initialTelefono: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(initialNombre) }
    var direccion by remember { mutableStateOf(initialDireccion) }
    var telefono by remember { mutableStateOf(initialTelefono) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del Cliente") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre, direccion, telefono) },
                enabled = nombre.isNotBlank() && direccion.isNotBlank() && telefono.isNotBlank()
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

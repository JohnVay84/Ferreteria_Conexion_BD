package com.example.conexin_bd_ferreteria.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.conexin_bd_ferreteria.data.model.Cliente
import com.example.conexin_bd_ferreteria.data.model.DetalleFactura
import com.example.conexin_bd_ferreteria.data.model.Factura
import com.example.conexin_bd_ferreteria.data.model.Pedido
import com.example.conexin_bd_ferreteria.data.model.Producto
import com.example.conexin_bd_ferreteria.ui.components.ClienteSection
import com.example.conexin_bd_ferreteria.ui.components.DashboardScreen
import com.example.conexin_bd_ferreteria.ui.components.FacturaSection
import com.example.conexin_bd_ferreteria.ui.components.LoginScreen
import com.example.conexin_bd_ferreteria.ui.components.PedidoSection
import com.example.conexin_bd_ferreteria.ui.components.ProductoSection
import com.example.conexin_bd_ferreteria.ui.theme.Conexión_BD_FerreteriaTheme

@Composable
fun FerreteriaAppScreen() {
    if (LocalInspectionMode.current) {
        FerreteriaAppPreview()
        return
    }

    val viewModel: FerreteriaViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    if (!uiState.isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { viewModel.login() }
        )
    } else {
        FerreteriaAppContent(
            uiState = uiState,
            onNavigateToDashboard = { viewModel.navigateToDashboard() },
            onNavigateToModule = { viewModel.navigateToModule(it) },
            onLogout = { viewModel.logout() },
            onAddCliente = { n, d, t -> viewModel.addCliente(n, d, t) },
            onUpdateCliente = { c -> viewModel.updateCliente(c) },
            onDeleteCliente = { id -> viewModel.deleteCliente(id) },
            onAddPedido = { d, f, cId -> viewModel.addPedido(d, f, cId) },
            onDeletePedido = { id -> viewModel.deletePedido(id) },
            onAddProducto = { fab, valP -> viewModel.addProducto(fab, valP) },
            onUpdateProducto = { prod -> viewModel.updateProducto(prod) },
            onDeleteProducto = { id -> viewModel.deleteProducto(id) },
            onAddFactura = { f, pId, det -> viewModel.addFactura(f, pId, det) },
            onDeleteFactura = { id -> viewModel.deleteFactura(id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FerreteriaAppContent(
    uiState: FerreteriaUiState,
    onNavigateToDashboard: () -> Unit,
    onNavigateToModule: (Int) -> Unit,
    onLogout: () -> Unit,
    onAddCliente: (String, String, String) -> Unit,
    onUpdateCliente: (Cliente) -> Unit,
    onDeleteCliente: (Int) -> Unit,
    onAddPedido: (String, String, Int) -> Unit,
    onDeletePedido: (Int) -> Unit,
    onAddProducto: (String, Double) -> Unit,
    onUpdateProducto: (Producto) -> Unit,
    onDeleteProducto: (Int) -> Unit,
    onAddFactura: (String, Int?, List<Pair<Int, Int>>) -> Unit,
    onDeleteFactura: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Aplicación Ferretería Don John",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (uiState.activeModuleIndex != -1) {
                        IconButton(onClick = onNavigateToDashboard) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al Menú")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = uiState.activeModuleIndex == -1,
                    onClick = onNavigateToDashboard,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = uiState.activeModuleIndex == 0,
                    onClick = { onNavigateToModule(0) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Clientes") },
                    label = { Text("Clientes") }
                )
                NavigationBarItem(
                    selected = uiState.activeModuleIndex == 1,
                    onClick = { onNavigateToModule(1) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Pedidos") },
                    label = { Text("Pedidos") }
                )
                NavigationBarItem(
                    selected = uiState.activeModuleIndex == 2,
                    onClick = { onNavigateToModule(2) },
                    icon = { Icon(Icons.Default.Build, contentDescription = "Productos") },
                    label = { Text("Productos") }
                )
                NavigationBarItem(
                    selected = uiState.activeModuleIndex == 3,
                    onClick = { onNavigateToModule(3) },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = "Facturas") },
                    label = { Text("Facturas") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (uiState.activeModuleIndex) {
                -1 -> DashboardScreen(
                    totalClientes = uiState.clientes.size,
                    totalPedidos = uiState.pedidos.size,
                    totalProductos = uiState.productos.size,
                    totalFacturas = uiState.facturas.size,
                    onNavigateToModule = onNavigateToModule
                )
                0 -> ClienteSection(
                    clientes = uiState.clientes,
                    onAddCliente = onAddCliente,
                    onUpdateCliente = onUpdateCliente,
                    onDeleteCliente = onDeleteCliente
                )
                1 -> PedidoSection(
                    pedidos = uiState.pedidos,
                    clientes = uiState.clientes,
                    onAddPedido = onAddPedido,
                    onDeletePedido = onDeletePedido
                )
                2 -> ProductoSection(
                    productos = uiState.productos,
                    onAddProducto = onAddProducto,
                    onUpdateProducto = onUpdateProducto,
                    onDeleteProducto = onDeleteProducto
                )
                3 -> FacturaSection(
                    facturas = uiState.facturas,
                    pedidos = uiState.pedidos,
                    productos = uiState.productos,
                    onAddFactura = onAddFactura,
                    onDeleteFactura = onDeleteFactura
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FerreteriaAppPreview() {
    val mockState = FerreteriaUiState(
        isLoggedIn = true,
        activeModuleIndex = -1,
        clientes = listOf(
            Cliente(1, "Carlos Rodríguez", "Av. Las Luces #452", "3001234567"),
            Cliente(2, "María Fernanda Gómez", "Calle 10 #12-34", "3159876543")
        ),
        productos = listOf(
            Producto(1, "Rodamiento SKF - 6204-2RSH", 28500.0),
            Producto(2, "Fierro Corrugado 1/2 in x 6m - Aceros Arequipa", 45000.0)
        ),
        pedidos = listOf(
            Pedido(1, "Suministro de rodamientos de precisión SKF", "2026-10-15", 1, "Carlos Rodríguez")
        ),
        facturas = listOf(
            Factura(
                codigoFactura = 1,
                fecha = "2026-10-15",
                valorFactura = 204000.0,
                pedidoId = 1,
                clienteNombre = "Carlos Rodríguez",
                clienteCodigoFormatted = "CLI000001",
                detalles = listOf(
                    DetalleFactura(1, 1, "Rodamiento SKF - 6204-2RSH", 4, 28500.0),
                    DetalleFactura(1, 4, "Platina de Fierro 1/4 x 2 in x 6m", 2, 89000.0)
                )
            )
        )
    )

    Conexión_BD_FerreteriaTheme {
        FerreteriaAppContent(
            uiState = mockState,
            onNavigateToDashboard = {},
            onNavigateToModule = {},
            onLogout = {},
            onAddCliente = { _, _, _ -> },
            onUpdateCliente = {},
            onDeleteCliente = {},
            onAddPedido = { _, _, _ -> },
            onDeletePedido = {},
            onAddProducto = { _, _ -> },
            onUpdateProducto = {},
            onDeleteProducto = {},
            onAddFactura = { _, _, _ -> },
            onDeleteFactura = {}
        )
    }
}

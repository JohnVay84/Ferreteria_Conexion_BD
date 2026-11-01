package com.example.conexin_bd_ferreteria.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.conexin_bd_ferreteria.data.database.FerreteriaDao
import com.example.conexin_bd_ferreteria.data.model.Cliente
import com.example.conexin_bd_ferreteria.data.model.Factura
import com.example.conexin_bd_ferreteria.data.model.Pedido
import com.example.conexin_bd_ferreteria.data.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FerreteriaUiState(
    val isLoggedIn: Boolean = false,
    val activeModuleIndex: Int = -1, // -1 = Dashboard de Módulos, 0 = Clientes, 1 = Pedidos, 2 = Productos, 3 = Facturas
    val clientes: List<Cliente> = emptyList(),
    val productos: List<Producto> = emptyList(),
    val pedidos: List<Pedido> = emptyList(),
    val facturas: List<Factura> = emptyList(),
    val toastMessage: String? = null
)

class FerreteriaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FerreteriaDao(application)

    private val _uiState = MutableStateFlow(FerreteriaUiState())
    val uiState: StateFlow<FerreteriaUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    fun login() {
        _uiState.update { it.copy(isLoggedIn = true, activeModuleIndex = -1) }
    }

    fun logout() {
        _uiState.update { it.copy(isLoggedIn = false, activeModuleIndex = -1) }
    }

    fun navigateToModule(index: Int) {
        _uiState.update { it.copy(activeModuleIndex = index) }
    }

    fun navigateToDashboard() {
        _uiState.update { it.copy(activeModuleIndex = -1) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun loadAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.seedInitialDataIfEmpty()
            val clientes = dao.getAllClientes()
            val productos = dao.getAllProductos()
            val pedidos = dao.getAllPedidos()
            val facturas = dao.getAllFacturas()

            _uiState.update {
                it.copy(
                    clientes = clientes,
                    productos = productos,
                    pedidos = pedidos,
                    facturas = facturas
                )
            }
        }
    }

    // ================= CLIENTES =================
    fun addCliente(nombre: String, direccion: String, telefono: String) {
        if (nombre.isBlank() || direccion.isBlank() || telefono.isBlank()) {
            _uiState.update { it.copy(toastMessage = "Por favor completa todos los campos del cliente.") }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.insertCliente(Cliente(nombre = nombre.trim(), direccion = direccion.trim(), telefono = telefono.trim()))
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Cliente registrado con éxito en SQLite.") }
                loadAllData()
            } else {
                _uiState.update { it.copy(toastMessage = "Error al insertar cliente.") }
            }
        }
    }

    fun updateCliente(cliente: Cliente) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.updateCliente(cliente)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Cliente actualizado.") }
                loadAllData()
            }
        }
    }

    fun deleteCliente(codigoCliente: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.deleteCliente(codigoCliente)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Cliente eliminado de SQLite.") }
                loadAllData()
            }
        }
    }

    // ================= PRODUCTOS =================
    fun addProducto(fabricante: String, valorProducto: Double) {
        if (fabricante.isBlank() || valorProducto <= 0) {
            _uiState.update { it.copy(toastMessage = "Ingrese un fabricante válido y un precio positivo.") }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.insertProducto(Producto(fabricante = fabricante.trim(), valorProducto = valorProducto))
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Producto registrado con éxito.") }
                loadAllData()
            }
        }
    }

    fun updateProducto(producto: Producto) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.updateProducto(producto)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Producto actualizado.") }
                loadAllData()
            }
        }
    }

    fun deleteProducto(codigoProducto: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.deleteProducto(codigoProducto)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Producto eliminado.") }
                loadAllData()
            }
        }
    }

    // ================= PEDIDOS =================
    fun addPedido(descripcion: String, fecha: String, clienteId: Int) {
        if (descripcion.isBlank() || fecha.isBlank() || clienteId <= 0) {
            _uiState.update { it.copy(toastMessage = "Selecciona un cliente y completa la descripción y fecha.") }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.insertPedido(
                Pedido(
                    descripcion = descripcion.trim(),
                    fecha = fecha.trim(),
                    clienteId = clienteId
                )
            )
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Pedido registrado correctamente.") }
                loadAllData()
            }
        }
    }

    fun deletePedido(codigoPedido: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.deletePedido(codigoPedido)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Pedido eliminado.") }
                loadAllData()
            }
        }
    }

    // ================= FACTURAS =================
    fun addFactura(fecha: String, pedidoId: Int?, detalles: List<Pair<Int, Int>>) {
        if (fecha.isBlank() || detalles.isEmpty()) {
            _uiState.update { it.copy(toastMessage = "Agrega al menos un producto a la factura y fecha válida.") }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.insertFacturaConDetalles(fecha = fecha.trim(), pedidoId = pedidoId, detalles = detalles)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Factura generada exitosamente en SQLite.") }
                loadAllData()
            }
        }
    }

    fun deleteFactura(codigoFactura: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.deleteFactura(codigoFactura)
            if (res > 0) {
                _uiState.update { it.copy(toastMessage = "Factura eliminada.") }
                loadAllData()
            }
        }
    }
}

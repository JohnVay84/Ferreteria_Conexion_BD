```mermaid
erDiagram
    Cliente {
        int codigo_cliente PK
        string nombre
        string direccion
        string telefono
    }
    Pedido {
        int codigo_pedido PK
        string descripcion
        string fecha
        int cliente_id FK
    }
    Producto {
        int codigo_producto PK
        string fabricante
        float valor_producto
    }
    Factura {
        int codigo_factura PK
        string fecha
        float valor_factura
        int pedido_id FK
    }
    Detalle_Factura {
        int codigo_factura PK
        int codigo_producto PK
        int cantidad
        float precio_unitario
    }

    Cliente ||--o{ Pedido : "realiza"
    Pedido ||--o| Factura : "genera"
    Factura ||--|{ Detalle_Factura : "contiene"
    Producto ||--o{ Detalle_Factura : "incluye"
package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(of = "producto", callSuper = false)
@Entity
public class DetallePedido extends Base {

    private int cantidad;
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    public DetallePedido(Long id, boolean eliminado, LocalDateTime createdAt,
                         int cantidad, Producto producto) {
        super(id, eliminado, createdAt);
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        this.subtotal = calcularSubtotal();
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = calcularSubtotal();
    }

    private Double calcularSubtotal() {
        if (producto == null) return 0.0;
        return producto.getPrecio() * this.cantidad;
    }
}

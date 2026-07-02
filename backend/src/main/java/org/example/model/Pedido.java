package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.model.enums.Estado;
import org.example.model.enums.FormaPago;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "detalles")
@ToString(exclude = "detalles")
@NoArgsConstructor
@SuperBuilder
@Entity
public class Pedido extends Base implements Calculable {

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private Double total;

    @Enumerated(EnumType.STRING)
    private FormaPago formaPago;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private Set<DetallePedido> detalles = new HashSet<>();

    public Pedido(Long id, boolean eliminado, LocalDateTime createdAt,
                  LocalDate fecha, Estado estado, FormaPago formaPago) {
        super(id, eliminado, createdAt);
        this.fecha = fecha;
        this.estado = estado;
        this.total = 0.0;
        this.formaPago = formaPago;
        this.detalles = new HashSet<>();
    }

    public Set<DetallePedido> getDetalles() {
        return Collections.unmodifiableSet(detalles);
    }

    @Override
    public void calcularTotal() {
        this.total = detalles.stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }

    public void addDetallePedido(int cantidad, Producto producto) {
        DetallePedido detalle = DetallePedido.builder()
                .eliminado(false)
                .createdAt(LocalDateTime.now())
                .cantidad(cantidad)
                .producto(producto)
                .subtotal(producto.getPrecio() * cantidad)
                .build();
        detalles.add(detalle);
        calcularTotal();
    }

    public DetallePedido findeDetallePedidoByProducto(Producto producto) {
        return detalles.stream()
                .filter(d -> d.getProducto().equals(producto))
                .findFirst()
                .orElse(null);
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        detalles.removeIf(d -> d.getProducto().equals(producto));
        calcularTotal();
    }
}

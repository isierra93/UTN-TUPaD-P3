package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "productos")
@EqualsAndHashCode(of = "nombre", callSuper = false)
@NoArgsConstructor
@SuperBuilder
@Entity
public class Categoria extends Base {

    private String nombre;
    private String descripcion;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Set<Producto> productos = new HashSet<>();

    public Categoria(Long id, boolean eliminado, LocalDateTime createdAt, String nombre, String descripcion) {
        super(id, eliminado, createdAt);
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.productos = new HashSet<>();
    }

    public Set<Producto> getProductos() {
        return Collections.unmodifiableSet(productos);
    }

    public boolean agregarProducto(Producto producto) {
        if (producto == null) return false;
        return productos.add(producto);
    }

    public boolean eliminarProducto(Producto producto) {
        if (producto == null) return false;
        return productos.remove(producto);
    }

    public boolean contieneProducto(Producto producto) {
        return productos.contains(producto);
    }
}

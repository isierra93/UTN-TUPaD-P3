package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode(of = "nombre", callSuper = false)
@Entity
public class Producto extends Base {

    private String nombre;
    private Double precio;
    private String descripcion;
    private Integer stock;
    private String imagen;
    private Boolean disponible;

    public Producto(Long id, boolean eliminado, LocalDateTime createdAt,
                    String nombre, Double precio, String descripcion,
                    Integer stock, String imagen, Boolean disponible) {
        super(id, eliminado, createdAt);
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.stock = stock;
        this.imagen = imagen;
        this.disponible = disponible;
    }
}

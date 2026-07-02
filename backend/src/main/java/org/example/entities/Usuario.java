package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.enums.Rol;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(of = "mail", callSuper = false)
@ToString(exclude = "pedidos")
@NoArgsConstructor
@SuperBuilder
@Entity
public class Usuario extends Base {

    private String nombre;
    private String apellido;
    private String mail;
    private String celular;
    private String clavePersonal;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Set<Pedido> pedidos = new HashSet<>();

    public Usuario(Long id, boolean eliminado, LocalDateTime createdAt,
                   String nombre, String apellido, String mail, String celular,
                   String clavePersonal, Rol rol) {
        super(id, eliminado, createdAt);
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.celular = celular;
        this.clavePersonal = clavePersonal;
        this.rol = rol;
        this.pedidos = new HashSet<>();
    }

    public Set<Pedido> getPedidos() {
        return Collections.unmodifiableSet(pedidos);
    }

    public boolean agregarPedido(Pedido pedido) {
        if (pedido == null) return false;
        return pedidos.add(pedido);
    }

    public boolean eliminarPedido(Pedido pedido) {
        if (pedido == null) return false;
        return pedidos.remove(pedido);
    }
}

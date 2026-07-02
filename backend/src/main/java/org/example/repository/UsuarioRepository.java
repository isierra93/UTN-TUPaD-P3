package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.Pedido;
import org.example.model.Producto;
import org.example.model.Usuario;
import org.example.model.enums.Estado;
import org.example.model.enums.FormaPago;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepository extends BaseRepository<Usuario>{

    public UsuarioRepository(){
        super(Usuario.class);
    }

    public Optional<Usuario> buscarPorMail(String mail){
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT u FROM Usuario u WHERE u.mail =:mail AND u.eliminado = false";
        try {
            TypedQuery<Usuario> q = em.createQuery(jpql, Usuario.class);
            List<Usuario> res = q.setParameter("mail", mail).getResultList();
            return res.isEmpty() ? Optional.empty() : Optional.of(res.getFirst());
        } finally {
            em.close();
        }
    }

    // Alta de pedido en una unica transaccion atomica. Recibe el id del usuario,
    // la forma de pago y un mapa {idProducto -> cantidad} (lista temporal sin entidades).
    // Recupera cada Producto gestionado con em.find dentro de esta transaccion, valida
    // disponibilidad y stock, arma los DetallePedido, reduce el stock y persiste el Pedido.
    // Si cualquier validacion falla se lanza excepcion y se hace rollback completo.
    public Pedido crearPedido(Long usuarioId, FormaPago formaPago, Map<Long, Integer> items) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null || usuario.isEliminado()) {
                throw new IllegalStateException("Usuario no encontrado.");
            }

            Pedido pedido = Pedido.builder()
                    .fecha(LocalDate.now())
                    .estado(Estado.PENDIENTE)
                    .formaPago(formaPago)
                    .eliminado(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            for (Map.Entry<Long, Integer> item : items.entrySet()) {
                Producto producto = em.find(Producto.class, item.getKey());
                int cantidad = item.getValue();

                if (producto == null || producto.isEliminado()) {
                    throw new IllegalStateException("Producto con ID " + item.getKey() + " no encontrado.");
                }
                if (!Boolean.TRUE.equals(producto.getDisponible())) {
                    throw new IllegalStateException("El producto '" + producto.getNombre() + "' no esta disponible.");
                }
                if (producto.getStock() < cantidad) {
                    throw new IllegalStateException("Stock insuficiente para '" + producto.getNombre()
                            + "' (disponible: " + producto.getStock() + ").");
                }

                pedido.addDetallePedido(cantidad, producto);
                producto.setStock(producto.getStock() - cantidad);
            }

            pedido.calcularTotal();
            usuario.agregarPedido(pedido);
            em.persist(pedido);

            em.getTransaction().commit();
            return pedido;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Pedido> buscarPedidosPorUsuario(Long idUsuario){
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT p FROM Usuario u JOIN u.pedidos p WHERE u.id = :idUsuario AND p.eliminado = false";
        try {
            return em.createQuery(jpql, Pedido.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

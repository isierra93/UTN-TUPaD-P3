package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.DetallePedido;
import org.example.model.Pedido;
import org.example.model.Producto;
import org.example.model.enums.Estado;

import java.util.List;

public class PedidoRepository extends BaseRepository<Pedido> {

    public PedidoRepository() {
        super(Pedido.class);
    }

    public List<Pedido> buscarPorEstado(Estado estado) {
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT p FROM Pedido p WHERE p.estado = :estado AND p.eliminado = false";
        try {
            return em.createQuery(jpql, Pedido.class)
                    .setParameter("estado", estado)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean cambiarEstado(Long id, Estado nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido == null || pedido.isEliminado()) return false;

            if (nuevoEstado == Estado.CANCELADO && pedido.getEstado() != Estado.CANCELADO) {
                for (DetallePedido detalle : pedido.getDetalles()) {
                    Producto prod = em.find(Producto.class, detalle.getProducto().getId());
                    if (prod != null) {
                        prod.setStock(prod.getStock() + detalle.getCantidad());
                    }
                }
            }

            pedido.setEstado(nuevoEstado);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

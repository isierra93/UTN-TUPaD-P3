package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Pedido;
import org.example.model.enums.Estado;

import java.util.List;

public class PedidoRepository extends BaseRepository<Pedido> {

    public PedidoRepository() {
        super(Pedido.class);
    }

    // Consulta JPQL: retorna todos los pedidos activos con un estado especifico.
    // Util para filtrar PENDIENTE, CONFIRMADO, TERMINADO o CANCELADO.
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

    // Cambia el estado de un pedido activo. Solo actualiza el campo estado; el stock
    // no se modifica. Retorna false si el pedido no existe o esta dado de baja.
    public boolean cambiarEstado(Long id, Estado nuevoEstado) {
        EntityManager em = emf.createEntityManager();
        try {
            Pedido pedido = em.find(Pedido.class, id);
            if (pedido == null || pedido.isEliminado()) return false;
            em.getTransaction().begin();
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

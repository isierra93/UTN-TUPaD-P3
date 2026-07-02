package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Pedido;
import org.example.model.enums.Estado;

import java.util.List;

public class PedidoRepository extends BaseRepository<Pedido>{

    public PedidoRepository(){
        super(Pedido.class);
    }

    public List<Pedido> buscarPorEstado(Estado estado){
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT p FROM Pedido p WHERE p.estado =:estado AND p.eliminado = false";

        try {
            return em.createQuery(jpql, Pedido.class)
                    .setParameter("estado", estado)
                    .getResultList();
        }finally {
            em.close();
        }
    }
}

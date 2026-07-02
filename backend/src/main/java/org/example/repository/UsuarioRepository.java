package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.entities.Pedido;
import org.example.entities.Usuario;

import java.util.List;

public class UsuarioRepository extends BaseRepository<Usuario>{

    public UsuarioRepository(){
        super(Usuario.class);
    }

    public List<Pedido> buscarPedidosPorUsuario(Long idUsuario){
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT p FROM Usuario u JOIN u.pedidos p "
                + "WHERE u.id = :uid AND p.eliminado = false";
        try {
            return em.createQuery(jpql, Pedido.class)
                    .setParameter("uid", idUsuario)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

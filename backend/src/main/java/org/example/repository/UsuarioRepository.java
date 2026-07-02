package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.DetallePedido;
import org.example.model.Pedido;
import org.example.model.Producto;
import org.example.model.Usuario;

import java.util.List;
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

    public Pedido crearPedido(Long usuarioId, Pedido pedido) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, usuarioId);
            if (usuario == null) return null;
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto managed = em.find(Producto.class, detalle.getProducto().getId());
                if (managed != null) detalle.setProducto(managed);
            }
            usuario.agregarPedido(pedido);
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

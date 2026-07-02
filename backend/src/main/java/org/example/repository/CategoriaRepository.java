package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.model.Categoria;
import org.example.model.Producto;

import java.util.List;

public class CategoriaRepository extends BaseRepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }

    public List<Producto> buscarProductosPorCategoria(Long categoriaId){
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT p FROM Categoria c JOIN c.productos p WHERE c.id = :categoriaId AND p.eliminado = false";
        try {
            return em.createQuery(jpql, Producto.class)
                    .setParameter("categoriaId", categoriaId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void agregarProducto(Long categoriaId, Producto producto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Categoria categoria = em.find(Categoria.class, categoriaId);
            if (categoria == null) return;
            categoria.agregarProducto(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean eliminarLogico(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria == null) return false;
            em.getTransaction().begin();
            categoria.setEliminado(true);
            categoria.getProductos().forEach(p -> p.setEliminado(true));
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

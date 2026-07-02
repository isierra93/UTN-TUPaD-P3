package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.model.Categoria;
import org.example.model.Producto;

import java.util.HashSet;
import java.util.Set;

public class CategoriaRepository extends BaseRepository<Categoria> {

    public CategoriaRepository() {
        super(Categoria.class);
    }

    public Set<Producto> buscarProductosPorCategoria(Categoria categoria){
        EntityManager em = emf.createEntityManager();
        String jpql = "SELECT p FROM Categoria c JOIN c.productos as p WHERE c = :categoria";
        try {
            TypedQuery<Producto> query = em.createQuery(jpql, Producto.class);
            query.setParameter("categoria", categoria);
            return new HashSet<>(query.getResultList());
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

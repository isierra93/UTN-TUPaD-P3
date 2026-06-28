package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.entities.Producto;

import java.util.List;

public class ProductoRepository extends BaseRepository<Producto> {

    public ProductoRepository() {
        super(Producto.class);
    }

    // Se sobreescribe para usar JOIN FETCH y evitar LazyInitializationException
    // al acceder a p.categoria fuera del contexto de persistencia.
    @Override
    public List<Producto> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Producto p LEFT JOIN FETCH p.categoria WHERE p.eliminado = false",
                    Producto.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    // Retorna todos los productos activos que pertenecen a la categoria indicada.
    // La consulta JPQL filtra por categoria.id = :categoriaId y eliminado = false,
    // usando un parametro nombrado para evitar SQL injection y garantizar tipado seguro.
    public List<Producto> buscarPorCategoria(Long categoriaId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery(
                    "SELECT p FROM Producto p " +
                    "WHERE p.categoria.id = :categoriaId AND p.eliminado = false",
                    Producto.class
            );
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

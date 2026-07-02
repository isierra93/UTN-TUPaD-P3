package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.model.Base;
import org.example.util.JPAUtil;

import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T extends Base> {

    protected final Class<T> clazz;
    protected final EntityManagerFactory emf;

    protected BaseRepository(Class<T> clazz) {
        this.clazz = clazz;
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    public T guardar(T entity) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = em.merge(entity);
            em.getTransaction().commit();
            return resultado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<T> buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(clazz, id));
        } finally {
            em.close();
        }
    }

    public List<T> listarActivos() {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT e FROM " + clazz.getSimpleName() + " e WHERE e.eliminado = false";
            return em.createQuery(jpql, clazz).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean eliminarLogico(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            T entity = em.find(clazz, id);
            if (entity == null) {
                return false;
            }
            em.getTransaction().begin();
            entity.setEliminado(true);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

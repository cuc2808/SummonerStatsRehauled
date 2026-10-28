package app.daos;

import app.entities.Champion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class ChampionDAO implements IDAO<Champion> {

    private final EntityManagerFactory emf;

    public ChampionDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Champion save(Champion champion) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(champion);
            em.getTransaction().commit();
            return champion;
        }
    }

    @Override
    public Set<Champion> get() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Champion c ORDER by c.name asc",
                    Champion.class
            ).getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public Champion getByID(String id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Champion.class, id);
        }
    }

    @Override
    public Champion update(Champion champion) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Champion updated = em.merge(champion);
            em.getTransaction().commit();
            return updated;
        }
    }

    @Override
    public void delete(Champion champion) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Champion managed = em.merge(champion);
            em.remove(managed);

            em.getTransaction().commit();
        }
    }
}
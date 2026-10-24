package app.daos;

import app.entities.MatchPreview;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static app.config.HibernateConfig.emf;

public class MatchPreviewDAO implements IDAO<MatchPreview> {

    @Override
    public MatchPreview save(MatchPreview matchPreview) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(matchPreview);
            em.getTransaction().commit();
            return matchPreview;
        }
    }

    @Override
    public Set<MatchPreview> get() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT m FROM MatchPreview m",
                    MatchPreview.class
            ).getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public MatchPreview getByID(String matchId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(MatchPreview.class, matchId);
        }
    }

    public Set<MatchPreview> getByPuuid(String puuid) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT m FROM MatchPreview m " +
                                    "WHERE m.puuid = :puuid " +
                                    "ORDER BY m.gameCreation DESC",
                            MatchPreview.class
                    )
                    .setParameter("puuid", puuid)
                    .setMaxResults(20)
                    .getResultStream()
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public MatchPreview update(MatchPreview matchPreview) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            MatchPreview updated = em.merge(matchPreview);
            em.getTransaction().commit();
            return updated;
        }
    }

    @Override
    public void delete(MatchPreview matchPreview) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            MatchPreview managed = em.merge(matchPreview);
            em.remove(managed);

            em.getTransaction().commit();
        }
    }

    public void deleteByPuuid(String puuid) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.createQuery(
                            "DELETE FROM MatchPreview m WHERE m.puuid = :puuid"
                    )
                    .setParameter("puuid", puuid)
                    .executeUpdate();

            em.getTransaction().commit();
        }
    }
}

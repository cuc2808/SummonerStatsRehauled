
package app.daos;

import app.entities.account.AccountIdentity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;
import static app.config.HibernateConfig.emf;

public class AccountIdentityDAO implements IDAO<AccountIdentity> {

    @Override
    public AccountIdentity save(AccountIdentity accountIdentity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(accountIdentity);
            em.getTransaction().commit();
            return accountIdentity;
        }
    }

    @Override
    public Set<AccountIdentity> get() {
        try (EntityManager em = emf.createEntityManager()) {
            return new HashSet<>(
                    em.createQuery(
                            "SELECT a FROM AccountIdentity a",
                            AccountIdentity.class
                    ).getResultList()
            );
        }
    }

    @Override
    public AccountIdentity getByID(String id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(AccountIdentity.class, id);
        }
    }

    @Override
    public AccountIdentity update(AccountIdentity accountIdentity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AccountIdentity updated = em.merge(accountIdentity);
            em.getTransaction().commit();
            return updated;
        }
    }

    @Override
    public void delete(AccountIdentity accountIdentity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            AccountIdentity managed =
                    em.find(AccountIdentity.class, accountIdentity.getId());

            if (managed != null) {
                em.remove(managed);
            }

            em.getTransaction().commit();
        }
    }

    public AccountIdentity getByPuuid(String puuid) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AccountIdentity a WHERE a.puuid = :puuid",
                            AccountIdentity.class
                    )
                    .setParameter("puuid", puuid)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    public AccountIdentity getByGameNameTagLineAndRegion(String gameName, String tagLine, String region) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AccountIdentity a " +
                                    "WHERE a.gameName = :gameName " +
                                    "AND a.tagLine = :tagLine " +
                                    "AND a.region = :region",
                            AccountIdentity.class
                    )
                    .setParameter("gameName", gameName)
                    .setParameter("tagLine", tagLine)
                    .setParameter("region", region)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }
}

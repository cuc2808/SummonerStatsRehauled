package app.daos;

import app.entities.account.AccountEntries;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;
import static app.config.HibernateConfig.emf;

public class AccountEntriesDAO implements IDAO<AccountEntries> {

    @Override
    public AccountEntries save(AccountEntries accountEntries) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(accountEntries);
            em.getTransaction().commit();
            return accountEntries;
        }
    }

    @Override
    public Set<AccountEntries> get() {
        try (EntityManager em = emf.createEntityManager()) {
            return new HashSet<>(
                    em.createQuery(
                            "SELECT a FROM AccountEntries a",
                            AccountEntries.class
                    ).getResultList()
            );
        }
    }

    @Override
    public AccountEntries getByID(String id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(AccountEntries.class, id);
        }
    }

    @Override
    public AccountEntries update(AccountEntries accountEntries) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AccountEntries updated = em.merge(accountEntries);
            em.getTransaction().commit();
            return updated;
        }
    }

    @Override
    public void delete(AccountEntries accountEntries) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            AccountEntries managed =
                    em.find(AccountEntries.class, accountEntries.getId());

            if (managed != null) {
                em.remove(managed);
            }

            em.getTransaction().commit();
        }
    }

    public Set<AccountEntries> getByPuuid(String puuid) {
        try (EntityManager em = emf.createEntityManager()) {
            return new HashSet<>(
                    em.createQuery(
                                    "SELECT a FROM AccountEntries a WHERE a.puuid = :puuid",
                                    AccountEntries.class
                            )
                            .setParameter("puuid", puuid)
                            .getResultList()
            );
        }
    }
}
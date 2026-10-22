package app.daos;

import app.entities.account.AccountState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;
import java.util.stream.Collectors;
import static app.config.HibernateConfig.emf;

public class AccountStateDAO implements IDAO<AccountState> {

    @Override
    public AccountState save(AccountState accountState) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(accountState);
            em.getTransaction().commit();
            return accountState;
        }
    }

    @Override
    public Set<AccountState> get() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT a FROM AccountState a",
                    AccountState.class
            ).getResultStream().collect(Collectors.toSet());
        }
    }

    @Override
    public AccountState getByID(String id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(AccountState.class, id);
        }
    }

    public AccountState getByPuuid(String puuid) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AccountState a WHERE a.puuid = :puuid",
                            AccountState.class
                    )
                    .setParameter("puuid", puuid)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public AccountState update(AccountState accountState) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AccountState updated = em.merge(accountState);
            em.getTransaction().commit();
            return updated;
        }
    }

    @Override
    public void delete(AccountState accountState) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            AccountState managed = em.merge(accountState);
            em.remove(managed);

            em.getTransaction().commit();
        }
    }
}
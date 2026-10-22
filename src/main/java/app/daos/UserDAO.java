package app.daos;

import app.config.HibernateConfig;
import app.dtos.accountdtos.AccountPreviewDTO;
import app.entities.Role;
import app.entities.User;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;

import static app.config.HibernateConfig.emf;

public class UserDAO implements IUserDAO {
    //Instances
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();

    public AccountPreviewDTO getAccountPreview(String puuid) {
        return em.createQuery("""
        SELECT new app.dtos.AccountPreviewDTO(
            ai.puuid,
            ai.gameName,
            ai.tagLine,
            ai.region,
            s.profileIconId,
            e.rank
        )
        FROM AccountOverview ao
        JOIN ao.accountIdentity ai
        JOIN ao.accountState s
        JOIN ao.accountEntries e
        WHERE ai.puuid = :puuid
        AND e.queueType = 'RANKED_SOLO_5x5'
        """, AccountPreviewDTO.class)
                .setParameter("puuid", puuid)
                .getSingleResult();
    }

    @Override
    public User getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = emf.createEntityManager()) {

            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            Hibernate.initialize(user.getRoles());

            if (user == null || !user.verifyPassword(password)) {
                return null;
            }

            return user;
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = emf.createEntityManager()) {

            User user = new User(username, password);
            Role userRole = em.createQuery(
                            "SELECT r FROM Role r WHERE r.role = :role",
                            Role.class
                    )
                    .setParameter("role", "USER")
                    .getResultStream()
                    .findFirst()
                    .orElseGet(() -> {
                        Role role = new Role("USER");
                        em.persist(role);
                        return role;
                    });

            user.addRole(userRole);

            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();

            return user;
        }
    }

    @Override
    public Role createRole(String role) {
        try (EntityManager em = emf.createEntityManager()) {

            Role newRole = new Role(role);

            em.getTransaction().begin();
            em.persist(newRole);
            em.getTransaction().commit();

            return newRole;
        }
    }

    @Override
    public User addUserRole(String username, String role) {
        try (EntityManager em = emf.createEntityManager()) {

            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            Role roleEntity = em.createQuery(
                            "SELECT r FROM Role r WHERE r.role = :role",
                            Role.class)
                    .setParameter("role", role)
                    .getSingleResult();

            em.getTransaction().begin();

            user.addRole(roleEntity);

            em.merge(user);

            em.getTransaction().commit();

            return user;
        }
    }
}

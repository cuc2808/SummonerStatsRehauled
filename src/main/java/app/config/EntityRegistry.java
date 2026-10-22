package app.config;

import app.entities.Champion;
import app.entities.MatchPreview;
import app.entities.Role;
import app.entities.User;
import app.entities.account.AccountEntries;
import app.entities.account.AccountIdentity;
import app.entities.account.AccountState;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(AccountEntries.class);
        configuration.addAnnotatedClass(AccountIdentity.class);
        configuration.addAnnotatedClass(AccountState.class);
        configuration.addAnnotatedClass(Champion.class);
        configuration.addAnnotatedClass(MatchPreview.class);
    }
}
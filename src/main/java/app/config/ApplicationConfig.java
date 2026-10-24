package app.config;

import app.controllers.AccountController;
import app.controllers.MatchController;
import app.daos.UserDAO;
import app.routes.AccountRoutes;
import app.routes.MatchRoutes;
import app.routes.SecurityRoutes;
import app.security.SecurityController;
import io.javalin.Javalin;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationConfig {

    public void appStart() {
        UserDAO userDAO = new UserDAO();

        String issuer = System.getenv("ISSUER") != null
                ? System.getenv("ISSUER")
                : "SummonerStats";

        int expireTimeInMillis = System.getenv("TOKEN_EXPIRE_TIME") != null
                ? Integer.parseInt(System.getenv("TOKEN_EXPIRE_TIME"))
                : 3600000;

        String secretKey = System.getenv("SECRET_KEY");

        SecurityController securityController = new SecurityController(
                userDAO,
                issuer,
                expireTimeInMillis,
                secretKey
        );
        AccountController accountController = new AccountController();
        MatchController matchController = new MatchController();

        SecurityRoutes securityRoutes = new SecurityRoutes(securityController);
        AccountRoutes accountRoutes = new AccountRoutes(accountController);
        MatchRoutes matchRoutes = new MatchRoutes(matchController);


        Javalin app = Javalin.create(config -> {
            config.router.apiBuilder(securityRoutes.getSecurityRoutes());
            config.router.apiBuilder(securityRoutes.getSecuredRoutes());
            config.router.apiBuilder(accountRoutes.getAccountRoutes());
            config.router.apiBuilder(matchRoutes.getAccountRoutes());
        });
        app.beforeMatched(securityController::authenticate); // check if there is a valid token in the header
        app.beforeMatched(securityController::authorize); // check if the user has the required role

        app.start(7070);
    }
}
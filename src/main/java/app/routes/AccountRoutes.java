package app.routes;

import app.controllers.AccountController;
import app.security.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class AccountRoutes {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final AccountController accountController;

    public AccountRoutes(AccountController accountController) {
        this.accountController = accountController;
    }

    public EndpointGroup getAccountRoutes() {
        return ()->{
            path("/account", ()->{
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Open")), SecurityRoutes.Role.ANYONE);
                post("/find", accountController::findAccount, SecurityRoutes.Role.ANYONE);
                post("/update", accountController::updateAccount, SecurityRoutes.Role.ANYONE);
            });
        };
    }

}

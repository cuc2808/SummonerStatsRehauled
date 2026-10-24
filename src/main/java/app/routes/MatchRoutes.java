package app.routes;

import app.controllers.AccountController;
import app.controllers.MatchController;
import app.security.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class MatchRoutes {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final MatchController matchController;

    public MatchRoutes(MatchController matchController) {
        this.matchController = matchController;
    }

    public EndpointGroup getAccountRoutes() {
        return ()->{
            path("match", ()->{
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Open")), SecurityRoutes.Role.ANYONE);
                post("/find", matchController::getMatchDetails, SecurityRoutes.Role.ANYONE);
            });
        };
    }

}


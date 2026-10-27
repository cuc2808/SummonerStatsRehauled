package app.routes;

import app.controllers.ChampionController;
import io.javalin.apibuilder.EndpointGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class ChampionRoutes {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final ChampionController championController;

    public ChampionRoutes (ChampionController championController) {this.championController = championController; }

    public EndpointGroup getChampionRoutes() {
        return () -> {
            path("/champion", () -> {
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Admin")), SecurityRoutes.Role.ADMIN);
                post("/update", championController::updateChampions, SecurityRoutes.Role.ADMIN);
            });
        };
    }

}

package app.routes;

import app.security.SecurityController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import io.javalin.security.RouteRole;

public class SecurityRoutes {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private final SecurityController securityController;

    public SecurityRoutes(SecurityController securityController) {
        this.securityController = securityController;
    }

    public EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", ()->{
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Open")),Role.ANYONE);
                post("/login", securityController::login, Role.ANYONE);
                post("/register", securityController::register, Role.ANYONE);
                get("/verify", securityController::verify, Role.ANYONE);
            });
        };
    }
    public EndpointGroup getSecuredRoutes(){
        return ()->{
            path("/protected", ()->{
                get("/user_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from USER Protected")),Role.USER);
                get("/admin_demo",(ctx)->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from ADMIN Protected")),Role.ADMIN);
            });
        };
    }
    public enum Role implements RouteRole { ANYONE, USER, ADMIN }
}
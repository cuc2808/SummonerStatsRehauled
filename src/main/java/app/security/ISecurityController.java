package app.security;

import io.javalin.http.Context;

public interface ISecurityController {
    void login(Context ctx); // to get a token
    void register(Context ctx); // to get a user
    void authenticate(Context ctx); // to verify roles inside token
    void authorize(Context ctx);
}

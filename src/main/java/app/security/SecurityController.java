package app.security;

import app.daos.UserDAO;
import app.dtos.UserDTO;
import app.entities.User;
import app.exceptions.ApiException;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ITokenSecurity tokenSecurity = new TokenSecurity();
    private final UserDAO userDAO;
    private final String issuer;
    private final int expireTimeInMillis;
    private final String secretKey;

    public SecurityController(UserDAO userDAO, String issuer, int expireTimeInMillis, String secretKey) {
        this.userDAO = userDAO;
        this.issuer = issuer;
        this.expireTimeInMillis = expireTimeInMillis;
        this.secretKey = secretKey;
    }

    @Override
    public void login(Context ctx) {
        UserDTO credentials = readCredentials(ctx);
        User user = userDAO.getVerifiedUser(credentials.getUsername(), credentials.getPassword());
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, SecurityMessages.UNAUTHORIZED_LOGIN);
        }
        respondWithToken(ctx, user);
    }

    @Override
    public void register(Context ctx) {
        UserDTO credentials = readCredentials(ctx);
        User user = userDAO.createUser(credentials.getUsername(), credentials.getPassword());
        respondWithToken(ctx, user);
    }

    @Override
    public void authenticate(Context ctx) {
        if (isOpenEndpoint(ctx)) {
            return;
        }
        ctx.attribute("user", verifyToken(getToken(ctx)));
    }

    @Override
    public void authorize(Context ctx) {
        if (isOpenEndpoint(ctx)) {
            return;
        }
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, SecurityMessages.NO_USER_FROM_TOKEN);
        }
        Set<String> allowedRoles = ctx.routeRoles().stream()
                .map(role -> role.toString().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
        boolean hasAccess = user.getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase(Locale.ROOT)));
        if (!hasAccess) {
            throw new ApiException(HttpStatus.FORBIDDEN, SecurityMessages.FORBIDDEN_ROLE + allowedRoles);
        }
    }

    public void verify(Context ctx) {
        ctx.status(HttpStatus.OK).json(verifyToken(getToken(ctx)));
    }

    private boolean isOpenEndpoint(Context ctx) {
        return ctx.method().toString().equals("OPTIONS")
                || ctx.routeRoles().isEmpty()
                || ctx.routeRoles().stream().anyMatch(role -> role.toString().equalsIgnoreCase("ANYONE"));
    }

    private String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        String[] parts = header == null ? new String[0] : header.strip().split("\\s+");
        if (parts.length != 2 || !parts[0].equalsIgnoreCase("Bearer") || parts[1].isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, SecurityMessages.MALFORMED_HEADER);
        }
        return parts[1];
    }

    private UserDTO verifyToken(String token) {
        try {
            return tokenSecurity.verifyToken(token, secretKey, issuer);
        } catch (TokenVerificationException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, SecurityMessages.INVALID_TOKEN);
        }
    }

    private UserDTO readCredentials(Context ctx) {
        UserDTO credentials;
        try {
            credentials = objectMapper.readValue(ctx.body(), UserDTO.class);
        } catch (JsonProcessingException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SecurityMessages.INVALID_CREDENTIALS);
        }
        if (credentials == null || credentials.getUsername() == null || credentials.getUsername().isBlank()
                || credentials.getPassword() == null || credentials.getPassword().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SecurityMessages.INVALID_CREDENTIALS);
        }
        return credentials;
    }

    private void respondWithToken(Context ctx, User user) {
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getRolesAsStrings());
        try {
            String token = tokenSecurity.createToken(userDTO, issuer, expireTimeInMillis, secretKey);
            ctx.status(HttpStatus.OK).json(Map.of("token", token, "username", user.getUsername()));
        } catch (TokenCreationException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create token.");
        }
    }
}

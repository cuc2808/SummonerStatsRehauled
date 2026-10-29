package app.security;

import app.daos.UserDAO;
import app.entities.User;
import app.exceptions.ApiException;
import app.exceptions.NotAuthorizedException;
import app.exceptions.TokenVerificationException;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import app.dtos.UserDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {
    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ITokenSecurity tokenSecurity = new TokenSecurity();
    private final UserDAO userDAO;
    private String issuer;
    private int expireTimeInMillis;
    private String secretKey;

    public SecurityController(UserDAO userDAO, String issuer, int expireTimeInMillis, String secretKey) {
        this.userDAO = userDAO;
        this.issuer = System.getenv("DEPLOYED") != null ? System.getenv("ISSUER") : issuer;
        this.expireTimeInMillis = System.getenv("DEPLOYED") != null ? Integer.parseInt(System.getenv("TOKEN_EXPIRE_TIME")) : expireTimeInMillis;
        this.secretKey = System.getenv("DEPLOYED") != null ? System.getenv("SECRET_KEY") : secretKey;
    }

    @Override
    public void login(Context ctx) {
        UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        User verifiedUser = userDAO.getVerifiedUser(username, password);
        String token = createToken(verifiedUser);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("token", token);
        response.put("username", verifiedUser.getUsername());

        logger.info("USER logged in - " + username);
        ctx.status(200).json(response);
    }

    @Override
    public void register(Context ctx) {
        UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        User newUser = userDAO.createUser(username, password);
        String token = createToken(newUser);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("token", token);
        response.put("username", newUser.getUsername());

        logger.info("USER registered - " + username);
        ctx.status(200).json(response);
    }

    @Override
    public void authenticate(Context ctx) {
        // This is a preflight request => no need for authentication
        if (ctx.method().toString().equals("OPTIONS")) {
            ctx.status(200);
            return;
        }
        // If the endpoint is not protected with roles or is open to ANYONE role, then skip
        Set<String> allowedRoles = ctx.routeRoles().stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
        if (isOpenEndpoint(allowedRoles))
            return;

        // If there is no token we do not allow entry
        UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);
        ctx.attribute("user", verifiedTokenUser); // -> ctx.attribute("user") in ApplicationConfig beforeMatched filter
    }

    @Override
    public void authorize(Context ctx) {
        Set<String> allowedRoles = ctx.routeRoles()
                .stream()
                .map(role -> role.toString().toUpperCase())
                .collect(Collectors.toSet());

        // 1. Check if the endpoint is open to all (either by not having any roles or having the ANYONE role set
        if (isOpenEndpoint(allowedRoles))
            return;
        // 2. Get user and ensure it is not null
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            throw new ForbiddenResponse("No user was added from the token");
        }
        // 3. See if any role matches
        if (!userHasAllowedRole(user, allowedRoles))
            throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
    }

    public void verify(Context ctx) {
        UserDTO user = validateAndGetUserFromToken(ctx);
        ctx.status(200).json(user);
    }

    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return verifiedTokenUser;
    }

    private UserDTO verifyToken(String token) {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | NotAuthorizedException | TokenVerificationException e) {
            logger.error("Could not create token", e);
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    private static String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null) {
            throw new UnauthorizedResponse("Authorization header is missing"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }

        // If the Authorization Header was malformed, then no entry
        String token = header.split(" ")[1];
        if (token == null) {
            throw new UnauthorizedResponse("Authorization header is malformed"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return token;
    }

    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        // If the endpoint is not protected with any roles:
        if (allowedRoles.isEmpty())
            return true;

        // 1. Get permitted roles and Check if the endpoint is open to all with the ANYONE role
        if (allowedRoles.contains("ANYONE")) {
            return true;
        }
        return false;
    }




    private static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        return user.getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }

    private String createToken(User user) {
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getRolesAsStrings());
        return tokenSecurity.createToken(userDTO, issuer, expireTimeInMillis, secretKey);
    }
}

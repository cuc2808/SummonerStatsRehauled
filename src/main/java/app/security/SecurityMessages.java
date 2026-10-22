package app.security;

/**
 * Purpose: To hold messages for security exceptions
 *
 * author: Thomas Hartmann
 */
public class SecurityMessages {
    public static final String INVALID_TOKEN = "Invalid user or token";
    public static final String UNAUTHORIZED_LOGIN = "Unauthorized. Could not verify user";
    public static final String USER_EXISTS = "User already exists";
    public static final String MALFORMED_HEADER = "Authorization header is missing or malformed";
    public static final String NO_USER_FROM_TOKEN = "No user was added from the token";
    public static final String FORBIDDEN_ROLE = "User was not authorized. Required roles: ";

    private SecurityMessages() { // Private constructor to prevent instantiation
    }
}
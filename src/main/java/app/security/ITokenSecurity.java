package app.security;

import app.dtos.UserDTO;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenVerificationException;

public interface ITokenSecurity {
    String createToken(UserDTO user, String issuer, int expireTimeInMillis, String secretKey)
            throws TokenCreationException;

    UserDTO verifyToken(String token, String secretKey, String issuer)
            throws TokenVerificationException;
}

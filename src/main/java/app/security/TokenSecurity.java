package app.security;

import app.dtos.UserDTO;
import app.exceptions.TokenCreationException;
import app.exceptions.TokenVerificationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class TokenSecurity implements ITokenSecurity {
    @Override
    public String createToken(UserDTO user, String issuer, int expireTimeInMillis, String secretKey)
            throws TokenCreationException {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .claim("username", user.getUsername())
                .claim("roles", String.join(",", user.getRoles()))
                .expirationTime(new Date(System.currentTimeMillis() + expireTimeInMillis))
                .build();
        try {
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secretKey));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new TokenCreationException("Could not create token", e);
        }
    }

    @Override
    public UserDTO verifyToken(String token, String secretKey, String issuer)
            throws TokenVerificationException {
        try {
            // Parse once and only read user details after verifying the signature.
            SignedJWT jwt = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())
                    || !jwt.verify(new MACVerifier(secretKey))) {
                throw new TokenVerificationException("Invalid token signature", null);
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            Date expiration = claims.getExpirationTime();
            if (expiration == null || !expiration.after(new Date()) || !issuer.equals(claims.getIssuer())) {
                throw new TokenVerificationException("Invalid token issuer or expiration", null);
            }

            String username = claims.getStringClaim("username");
            String roles = claims.getStringClaim("roles");
            if (username == null || username.isBlank() || roles == null || roles.isBlank()) {
                throw new TokenVerificationException("Missing token user or roles", null);
            }
            Set<String> rolesSet = Arrays.stream(roles.split(","))
                    .map(String::strip)
                    .filter(role -> !role.isBlank())
                    .collect(Collectors.toSet());
            if (rolesSet.isEmpty()) {
                throw new TokenVerificationException("Missing token roles", null);
            }
            return new UserDTO(username, rolesSet);
        } catch (ParseException | JOSEException e) {
            throw new TokenVerificationException("Could not verify token", e);
        }
    }
}

package sit.int221.itbkkbackend.auth.utils;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
public class JwksTokenUtil implements Serializable {
    private final String jwksUrl;

    public JwksTokenUtil(@Value("${jwks.url}") String jwksUrl) {
        this.jwksUrl = jwksUrl;
    }
    public Boolean validateJWKS(String token)  {
        try {
            // Parse the JWT
            SignedJWT signedJWT = SignedJWT.parse(token);
            String kid = signedJWT.getHeader().getKeyID();

            // Fetch the JWK set from Azure AD
            JWKSet jwkSet = JWKSet.load(new URL(jwksUrl));

            // Find the key with the matching 'kid'
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) {
                throw new IllegalArgumentException("Public key not found for 'kid': " + kid);
            }

            // Cast to RSA key and get the public key
            RSAKey rsaKey = (RSAKey) jwk;
            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

            // Verify the signature
            boolean isValid = signedJWT.verify(new com.nimbusds.jose.crypto.RSASSAVerifier(publicKey));
            return isValid;
        } catch (Exception e){
            return false;
        }
    }

    private JWTClaimsSet getAllClaims(String token){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        }  catch (Exception e) {
            throw new MalformedJwtException("Token is malformed.");
        }
    }

    public String getClaimsByName(String token,String claimName) throws ParseException {
        JWTClaimsSet claims = getAllClaims(token);
        return claims.getStringClaim(claimName);
    }


    private Boolean isTokenExpired(String token) {
        JWTClaimsSet claims = getAllClaims(token);
        final Date expiration = claims.getExpirationTime();
        return expiration.before(new Date());
    }

    public String getOidFromToken(String token){
        if(!validateJWKS(token)){
            throw new SignatureException("Invalid token signature.");
        }
        if(isTokenExpired(token)){
            throw new ExpiredJwtException(null,null,"Token is expired");
        }
        try {
            return getClaimsByName(token,"oid");
        } catch (Exception e){
            throw new MalformedJwtException("Token is malformed");
        }

    }
}




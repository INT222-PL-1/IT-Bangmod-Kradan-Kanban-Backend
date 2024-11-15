package sit.int221.itbkkbackend.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sit.int221.itbkkbackend.auth.entities.Users;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    private final String accessSecretKey;
    private final String refreshSecretKey;
    private final long jwtAccessTokenValidity;
    private final long jwtRefreshTokenValidity;

    public JwtTokenUtil(@Value("${jwt.secret.access}") String accessSecretKey,
                        @Value("${jwt.secret.refresh}") String refreshSecretKey,
                        @Value("#{${jwt.max-token-interval-hour}*60*60*1000}") long jwtAccessTokenValidity,
                        @Value("#{${jwt.max-refresh-token-interval-hour}*60*60*1000}") long jwtRefreshTokenValidity) {
        this.accessSecretKey = accessSecretKey;
        this.refreshSecretKey = refreshSecretKey;
        this.jwtAccessTokenValidity = jwtAccessTokenValidity;
        this.jwtRefreshTokenValidity = jwtRefreshTokenValidity;
    }

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public enum TokenType{
        ACCESS , REFRESH
    }

    public String getOidFromToken(String token,TokenType tokenType){
        return getAllClaimsFromToken(token,tokenType).get("oid", String.class);
    }

    public Date getExpirationDateFromToken(String token,TokenType tokenType) {
        return getClaimFromToken(token, Claims::getExpiration,tokenType);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver,TokenType tokenType) {
        final Claims claims = getAllClaimsFromToken(token,tokenType);
        return claimsResolver.apply(claims);
    }

    public Claims getAllClaimsFromToken(String token,TokenType tokenType) {
        String secret = tokenType == TokenType.ACCESS ? accessSecretKey : refreshSecretKey;
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token,TokenType tokenType) {
        final Date expiration = getExpirationDateFromToken(token,tokenType);
        return expiration.before(new Date());
    }

    public Boolean validateToken(String token,TokenType tokenType) {
        return  !isTokenExpired(token,tokenType);
    }

    public String generateAccessToken(Users user, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("oid", user.getOid());
        if(tokenType == TokenType.ACCESS){
            claims.put("role", user.getRole());
            claims.put("name", user.getName());
            claims.put("email", user.getEmail());
        }
        long validity = tokenType == TokenType.ACCESS ? jwtAccessTokenValidity : jwtRefreshTokenValidity;
        String secret = tokenType == TokenType.ACCESS ? accessSecretKey : refreshSecretKey;
        return doGenerateToken(claims, validity,secret);
    }


    private String doGenerateToken(Map<String, Object> claims ,long validity,String secret) {
        return Jwts.builder().setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/pl1")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), signatureAlgorithm)
                .compact();
    }

}

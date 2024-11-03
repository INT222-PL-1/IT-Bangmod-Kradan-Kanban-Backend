package sit.int221.itbkkbackend.auth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
    @Value("${jwt.access-secret}")
    private String ACCESS_SECRET_KEY;
    @Value("${jwt.refresh-secret}")
    private String REFRESH_SECRET_KEY;
    @Value("#{${jwt.max-token-interval-hour}*60*60*1000}")
    private long JWT_ACCESS_TOKEN_VALIDITY;
    @Value("#{${jwt.max-refresh-token-interval-hour}*60*60*1000}")
    private long JWT_REFRESH_TOKEN_VALIDITY;
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
        String secret = tokenType == TokenType.ACCESS ? ACCESS_SECRET_KEY : REFRESH_SECRET_KEY;
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims;
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
        long validity = tokenType == TokenType.ACCESS ? JWT_ACCESS_TOKEN_VALIDITY : JWT_REFRESH_TOKEN_VALIDITY;
        String secret = tokenType == TokenType.ACCESS ? ACCESS_SECRET_KEY : REFRESH_SECRET_KEY;
        return doGenerateToken(claims, validity,secret);
    }


    private String doGenerateToken(Map<String, Object> claims ,long validity,String secret) {
        return Jwts.builder().setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/pl1")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(signatureAlgorithm, secret).compact();
    }

}

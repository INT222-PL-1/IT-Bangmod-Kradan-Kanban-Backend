package sit.int221.itbkkbackend.controllers;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.*;

@RestController
@RequestMapping("/token")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4173",
        "http://ip23pl1.sit.kmutt.ac.th:5173",
        "http://ip23pl1.sit.kmutt.ac.th:3000",
        "http://ip23pl1.sit.kmutt.ac.th:4173",
        "http://ip23pl1.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th"
})
public class ValidateTokenController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken() {
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("")
    public ResponseEntity<Object> requestAccessToken(@RequestHeader(name = "refresh_token") String refreshToken) {
        if (refreshToken != null) {
            try {
                jwtTokenUtil.validateToken(refreshToken, JwtTokenUtil.TokenType.REFRESH);
                String oid = jwtTokenUtil.getOidFromToken(refreshToken, JwtTokenUtil.TokenType.REFRESH);
                Users user = usersRepository.findByOid(oid);
                String accessToken = jwtTokenUtil.generateAccessToken(user, JwtTokenUtil.TokenType.ACCESS);
//                String newRefreshToken = jwtTokenUtil.generateAccessToken();
                return ResponseEntity.ok().body(new Token(accessToken));
            }
//            catch (IllegalArgumentException | MalformedJwtException e) {
//                    request.setAttribute("errorType", ErrorType.TOKEN_NOT_WELL_FORMED);
//            } catch (ExpiredJwtException e) {
//                    request.setAttribute("errorType", ErrorType.TOKEN_EXPIRED);
//            } catch (SignatureException e){
//                    request.setAttribute("errorType", ErrorType.TOKEN_TAMPERED);
//            }
            catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}

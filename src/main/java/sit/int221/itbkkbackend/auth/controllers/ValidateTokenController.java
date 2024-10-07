package sit.int221.itbkkbackend.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.dtos.Token;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.auth.utils.ErrorType;
import sit.int221.itbkkbackend.auth.utils.JwtTokenUtil;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;

import java.sql.Timestamp;
import java.util.Map;

@Slf4j
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
        "http://intproj23.sit.kmutt.ac.th",
        "https://ip23pl1.sit.kmutt.ac.th:5173",
        "https://ip23pl1.sit.kmutt.ac.th:3000",
        "https://ip23pl1.sit.kmutt.ac.th:4173",
        "https://ip23pl1.sit.kmutt.ac.th",
        "https://intproj23.sit.kmutt.ac.th"
},allowCredentials = "true")
public class ValidateTokenController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UsersRepository usersRepository;

    @GetMapping("/validate")
    public ResponseEntity<Object> validateToken() {
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("")
    public ResponseEntity<Object> requestAccessToken(@RequestHeader(name = "x-refresh-token") String refreshToken, @RequestBody(required = false) Map<String,String> userData, HttpServletRequest request) {
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
                return ResponseEntity.status(401).body(
                        new ErrorResponse(
                                ErrorType.REFRESH_TOKEN_INVALID,
                                new Timestamp(System.currentTimeMillis()),
                                401,
                                ErrorType.REFRESH_TOKEN_INVALID.getMessage(),
                                request.getRequestURI()
                        )
                );
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}

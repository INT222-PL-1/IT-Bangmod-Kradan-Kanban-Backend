package sit.int221.itbkkbackend.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.dtos.Token;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.auth.utils.JwtTokenUtil;
import sit.int221.itbkkbackend.auth.utils.enums.ErrorType;
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
        "https://intproj23.sit.kmutt.ac.th",
        "https://20.243.133.115"
},allowCredentials = "true")
public class ValidateTokenController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersRepository usersRepository;

    public ValidateTokenController(JwtTokenUtil jwtTokenUtil, UsersRepository usersRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.usersRepository = usersRepository;
    }

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
                return ResponseEntity.ok().body(new Token(accessToken));
            }
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

package sit.int221.itbkkbackend.auth.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.dtos.LoginRequestDTO;
import sit.int221.itbkkbackend.auth.dtos.Token;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.auth.utils.JwtTokenUtil;
import sit.int221.itbkkbackend.v3.entities.UserV3;
import sit.int221.itbkkbackend.v3.repositories.UserRepositoryV3;
import sit.int221.itbkkbackend.v3.services.ValidatingServiceV3;

@RestController
@RequestMapping("/login")
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
}, allowCredentials = "true")
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final ValidatingServiceV3 validatingService;
    private final UsersRepository usersRepository;
    private final ModelMapper mapper;
    private final UserRepositoryV3 userRepositoryV3;

    public AuthController(JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager, ValidatingServiceV3 validatingService, UsersRepository usersRepository, ModelMapper mapper, UserRepositoryV3 userRepositoryV3) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.validatingService = validatingService;
        this.usersRepository = usersRepository;
        this.mapper = mapper;
        this.userRepositoryV3 = userRepositoryV3;
    }

    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO jwtRequestUser, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        validatingService.validateLoginRequestDTO(jwtRequestUser);

        try{
            
            Users user = usersRepository.findByUsername(jwtRequestUser.getUserName());
            String token = jwtTokenUtil.generateAccessToken(user, JwtTokenUtil.TokenType.ACCESS);
            String refreshToken = jwtTokenUtil.generateAccessToken(user, JwtTokenUtil.TokenType.REFRESH);

            String userOid = user.getOid();

            authenticationManager.authenticate(authenticationToken);

            // Check if user is not registered in UserV3
            if(!userRepositoryV3.existsById(userOid)){
                UserV3 regisUser = mapper.map(user, UserV3.class);
                userRepositoryV3.save(regisUser);
            }

            Token accessAndRefreshToken = new Token(token, refreshToken);
            return ResponseEntity.ok().body(accessAndRefreshToken);
        }
        catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is incorrect");
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There is a problem. Please try again later.");
        }
    }

}
package sit.int221.itbkkbackend.controllers;

import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.*;
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
        "http://intproj23.sit.kmutt.ac.th"
})
public class AuthController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    ValidatingServiceV3 validatingService;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    ModelMapper mapper;
    @Autowired
    UserRepositoryV3 userRepositoryV3;

    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO jwtRequestUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        validatingService.validateLoginRequestDTO(jwtRequestUser);
        try{

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            Users user = usersRepository.findByUsername(jwtRequestUser.getUserName());
            String token = jwtTokenUtil.generateAccessToken(user, JwtTokenUtil.TokenType.ACCESS);
            String refreshToken = jwtTokenUtil.generateAccessToken(user, JwtTokenUtil.TokenType.REFRESH);
            if(userRepositoryV3.existsById(user.getOid()) == false){
                UserV3 regisUser = mapper.map(user, UserV3.class);
                userRepositoryV3.save(regisUser);
            }
            return ResponseEntity.ok().body(new Token(token,refreshToken));
        }
        catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or Password is incorrect");
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"There is a problem. Please try again later.");
        }
    }

}
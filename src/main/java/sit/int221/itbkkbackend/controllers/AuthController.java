package sit.int221.itbkkbackend.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.LoginRequestDTO;
import sit.int221.itbkkbackend.v2.services.ValidatingServiceV2;

@RestController
@RequestMapping("/v2/login")
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
    AuthenticationManager authenticationManager;
    @Autowired
    ValidatingServiceV2 validatingService;
    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO jwtRequestUser) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword());
        try{
            validatingService.validateLoginRequestDTO(jwtRequestUser);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
        }catch (ConstraintViolationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username or Password is incorrect");
        }
        catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or Password is incorrect");
        }
        return ResponseEntity.ok("");
    }


}

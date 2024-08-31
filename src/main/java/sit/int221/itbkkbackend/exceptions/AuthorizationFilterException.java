package sit.int221.itbkkbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;

public class AuthorizationFilterException extends AuthenticationException {
    public AuthorizationFilterException(){
        super("Access Denied pls try again");
    }

}

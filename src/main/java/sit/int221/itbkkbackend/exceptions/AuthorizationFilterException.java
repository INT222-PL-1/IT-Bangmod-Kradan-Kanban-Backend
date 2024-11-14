package sit.int221.itbkkbackend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthorizationFilterException extends AuthenticationException {
    public AuthorizationFilterException(){
        super("Access Denied pls try again");
    }

}

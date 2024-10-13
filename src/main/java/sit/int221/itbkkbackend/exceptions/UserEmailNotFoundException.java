package sit.int221.itbkkbackend.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UserEmailNotFoundException extends ResponseStatusException {


    public UserEmailNotFoundException(HttpStatusCode status, String reason) {
        super(status, String.format("User with %s email does not exist !!!",reason));
    }
}

package sit.int221.itbkkbackend.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class DeleteItemNotFoundException extends ResponseStatusException{

    public DeleteItemNotFoundException(HttpStatusCode status) {
        super(status);
    }
}

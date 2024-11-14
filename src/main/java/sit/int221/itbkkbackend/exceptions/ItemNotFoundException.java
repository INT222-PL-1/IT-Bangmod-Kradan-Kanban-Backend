package sit.int221.itbkkbackend.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {

    public ItemNotFoundException(HttpStatusCode status, Integer id) {
        super(status, String.format("Task id %d does not exist !!!",id));
    }

}

package sit.int221.itbkkbackend.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateStatusNameException extends ResponseStatusException {
    public DuplicateStatusNameException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}

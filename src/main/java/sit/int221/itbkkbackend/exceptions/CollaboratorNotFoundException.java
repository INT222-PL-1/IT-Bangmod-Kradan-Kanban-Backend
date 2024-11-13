package sit.int221.itbkkbackend.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class CollaboratorNotFoundException extends ResponseStatusException {
    public CollaboratorNotFoundException(HttpStatusCode status, String reason) {
        super(status, String.format("Collaborator id %s does not exist in current board",reason));
    }
}

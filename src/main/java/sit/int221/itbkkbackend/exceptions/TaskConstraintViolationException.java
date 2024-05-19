package sit.int221.itbkkbackend.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.webmvc.support.RepositoryConstraintViolationExceptionMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class TaskConstraintViolationException extends ConstraintViolationException {
    private Map<String,String> additionalErrorFields = new HashMap<>();

    public TaskConstraintViolationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(constraintViolations);
    }

}

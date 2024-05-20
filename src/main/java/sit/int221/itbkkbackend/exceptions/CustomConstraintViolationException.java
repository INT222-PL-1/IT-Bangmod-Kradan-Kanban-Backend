package sit.int221.itbkkbackend.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
public class CustomConstraintViolationException extends ConstraintViolationException {
    private Map<String,String> additionalErrorFields = new HashMap<>();

    public CustomConstraintViolationException(Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(constraintViolations);
    }

}

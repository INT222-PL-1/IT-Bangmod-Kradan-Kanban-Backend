package sit.int221.itbkkbackend.v2.services;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;

import java.util.Collections;

@Service
@Validated
public class ValidatingServiceV2 {

    void validateTaskDTO(@Valid TaskDTO task,Boolean isStatusExist){
        if (!isStatusExist){
            throw new ConstraintViolationException(Collections.emptySet());
        }
    }

    void validateStatusDTO(@Valid StatusDTO status,Boolean isDuplicate){
        if(isDuplicate){
            throw new ConstraintViolationException(Collections.emptySet());
        }
    }
}

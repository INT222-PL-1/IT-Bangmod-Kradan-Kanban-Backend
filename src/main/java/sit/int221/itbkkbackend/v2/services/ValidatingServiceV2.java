package sit.int221.itbkkbackend.v2.services;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.auth.LoginRequestDTO;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;

import java.util.Collections;

@Service
@Validated
public class ValidatingServiceV2 {

    public void validateTaskDTO(@Valid TaskDTO task,Boolean isStatusExist){
        if (!isStatusExist){
            throw new ConstraintViolationException(Collections.emptySet());
        }
    }

    public void validateStatusDTO(@Valid StatusDTO status,Boolean isDuplicate){
        if(isDuplicate){
            throw new ConstraintViolationException(Collections.emptySet());
        }
    }

    public void validateLoginRequestDTO(@Valid LoginRequestDTO user){}
}

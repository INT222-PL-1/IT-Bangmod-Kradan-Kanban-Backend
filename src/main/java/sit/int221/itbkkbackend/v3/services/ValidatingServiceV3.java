package sit.int221.itbkkbackend.v3.services;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.auth.LoginRequestDTO;
import sit.int221.itbkkbackend.v3.dtos.BoardDTO;
import sit.int221.itbkkbackend.v3.dtos.StatusDTO;
import sit.int221.itbkkbackend.v3.dtos.TaskDTO;
import java.util.Collections;

@Service
@Validated
public class ValidatingServiceV3 {

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

    public void validateBoardDTO(@Valid BoardDTO board){}
}
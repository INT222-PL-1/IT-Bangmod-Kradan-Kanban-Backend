package sit.int221.itbkkbackend.v3.services;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.auth.dtos.LoginRequestDTO;
import sit.int221.itbkkbackend.v3.dtos.*;

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

    public void validateCollaboratorDTO(@Valid CollaboratorDTO collaborator){}

    public void validateAddCollaboratorDTO(@Valid AddCollaboratorDTO collaborator){}

    public void validateUpdateCollaboratorDTO(@Valid UpdateCollaboratorDTO collaborator){}
}
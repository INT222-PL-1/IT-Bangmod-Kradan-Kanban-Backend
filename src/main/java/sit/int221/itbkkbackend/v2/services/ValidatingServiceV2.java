package sit.int221.itbkkbackend.v2.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.v2.dtos.SaveTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;

@Service
@Validated
public class ValidatingServiceV2 {
    void validateTaskDTO(@Valid TaskDTO task){};
    void validateSaveTaskDTO(@Valid SaveTaskDTO task){};
    void validateStatusDTO(@Valid StatusDTO status){};
}

package sit.int221.itbkkbackend.v2.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.v2.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;

@Service
@Validated
public class ValidatingServiceV2 {
    void validateTaskDTO(TaskDTO task){};
}

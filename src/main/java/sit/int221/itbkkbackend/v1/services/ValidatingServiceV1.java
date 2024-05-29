package sit.int221.itbkkbackend.v1.services;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.v1.dtos.TaskDTO;

@Service
@Validated
public class ValidatingServiceV1 {
    void validateTaskDTO(@Valid TaskDTO task){}
}

package sit.int221.itbkkbackend.services;


import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import sit.int221.itbkkbackend.dtos.TaskDTO;

@Service
@Validated
public class ValidatingService {
    void validateTaskDTO(@Valid TaskDTO task){};
}

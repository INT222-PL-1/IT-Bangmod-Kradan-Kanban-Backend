package sit.int221.itbkkbackend.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.dtos.TaskDTO;
import sit.int221.itbkkbackend.entities.Task;
import sit.int221.itbkkbackend.services.TaskService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4173",
        "http://ip23pl1.sit.kmutt.ac.th:5173",
        "http://ip23pl1.sit.kmutt.ac.th:3000",
        "http://ip23pl1.sit.kmutt.ac.th:4173",
        "http://ip23pl1.sit.kmutt.ac.th"
})
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<SimpleTaskDTO> getAllTasks(){
        return service.getAllSimpleTasksDTO();
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(@PathVariable Integer id){
        return service.getTaskDTOById(id);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Task addTask(@RequestBody TaskDTO task){
        return service.addTask(task);
    }

    @DeleteMapping("/{id}")
    public SimpleTaskDTO deleteTask(@PathVariable Integer id){
        return service.deleteTaskById(id);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Integer id ,@RequestBody Task task){
        return service.updateTaskById(id,task);
    }

    @PatchMapping("/{id}")
    public Task updatePartialTask(@PathVariable Integer id ,@RequestBody Map<String, Optional<Object>> task){
        return service.updatePartialTaskById(id,task);
    }
}

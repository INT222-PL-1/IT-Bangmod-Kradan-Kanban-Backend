package sit.int221.itbkkbackend.v1.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v1.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v1.dtos.TaskDTO;
import sit.int221.itbkkbackend.v1.entities.TaskV1;
import sit.int221.itbkkbackend.v1.services.TaskServiceV1;

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
        "http://ip23pl1.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th",
        "https://ip23pl1.sit.kmutt.ac.th:5173",
        "https://ip23pl1.sit.kmutt.ac.th:3000",
        "https://ip23pl1.sit.kmutt.ac.th:4173",
        "https://ip23pl1.sit.kmutt.ac.th",
        "https://intproj23.sit.kmutt.ac.th"
})
@RestController
@RequestMapping("/v1/tasks")
public class TaskControllerV1 {
    @Autowired
    private TaskServiceV1 service;

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
    public TaskV1 addTask(@RequestBody TaskDTO task){
        return service.addTask(task);
    }

    @DeleteMapping("/{id}")
    public SimpleTaskDTO deleteTask(@PathVariable Integer id){
        return service.deleteTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskV1 updateTask(@PathVariable Integer id , @RequestBody TaskV1 task){
        return service.updateTaskById(id,task);
    }

    @PatchMapping("/{id}")
    public TaskV1 updatePartialTask(@PathVariable Integer id , @RequestBody Map<String, Optional<Object>> task){
        return service.updatePartialTaskById(id,task);
    }
}

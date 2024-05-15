package sit.int221.itbkkbackend.v2.controllers;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v2.dtos.SaveTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;
import sit.int221.itbkkbackend.v2.entities.TaskV2;
import sit.int221.itbkkbackend.v2.services.TaskServiceV2;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4173",
        "http://ip23pl1.sit.kmutt.ac.th:5173",
        "http://ip23pl1.sit.kmutt.ac.th:3000",
        "http://ip23pl1.sit.kmutt.ac.th:4173",
        "http://ip23pl1.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th"
})
@RestController
@RequestMapping("/v2/tasks")
public class TaskControllerV2 {
    @Autowired
    private TaskServiceV2 service;

    @GetMapping("")
    public List<SimpleTaskDTO> getAllTasks(@RequestParam(defaultValue = "createdOn") String sortBy ,@RequestParam(defaultValue = "ASC") String sortDirection, @RequestParam(required = false) ArrayList<String> filterStatuses){
        return service.getAllSimpleTasksDTO(sortBy,sortDirection,filterStatuses);
    }

    @GetMapping("/{id}")
    public TaskV2 getTask(@PathVariable Integer id){
        return service.getTaskById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public TaskDTO addTask(@RequestBody SaveTaskDTO task){
        return service.addTask(task);
    }

    @DeleteMapping("/{id}")
    public SimpleTaskDTO deleteTask(@PathVariable Integer id){
        return service.deleteTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Integer id ,@RequestBody TaskDTO task){
        return service.updateTaskById(id,task);
    }

}

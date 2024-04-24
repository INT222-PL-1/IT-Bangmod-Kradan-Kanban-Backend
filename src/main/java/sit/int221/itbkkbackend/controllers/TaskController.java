package sit.int221.itbkkbackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.dtos.TaskDTO;
import sit.int221.itbkkbackend.services.TaskService;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<SimpleTaskDTO> getAllTasks(){
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Integer id){
        return service.getTaskById(id);
    }
}

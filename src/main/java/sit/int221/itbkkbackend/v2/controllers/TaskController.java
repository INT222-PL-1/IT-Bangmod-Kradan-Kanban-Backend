package sit.int221.itbkkbackend.v2.controllers;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v2.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;
import sit.int221.itbkkbackend.v2.entities.Task;
import sit.int221.itbkkbackend.v2.services.TaskService;

import java.util.List;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/v2/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<SimpleTaskDTO> getAllTasks(){
        return service.getAllSimpleTasksDTO();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Integer id){
        return service.getTaskById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public TaskDTO addTask(@RequestBody TaskDTO task){
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

//    @PatchMapping("/{id}")
//    public Task updatePartialTask(@PathVariable Integer id ,@RequestBody Map<String, Optional<Object>> task){
//        return service.updatePartialTaskById(id,task);
//    }
}

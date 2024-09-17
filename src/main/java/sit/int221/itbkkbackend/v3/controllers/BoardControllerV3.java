package sit.int221.itbkkbackend.v3.controllers;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v3.dtos.BoardDTO;
import sit.int221.itbkkbackend.v3.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v3.dtos.StatusDTO;
import sit.int221.itbkkbackend.v3.dtos.TaskDTO;
import sit.int221.itbkkbackend.v3.entities.BoardV3;
import sit.int221.itbkkbackend.v3.entities.StatusV3;
import sit.int221.itbkkbackend.v3.entities.TaskV3;
import sit.int221.itbkkbackend.v3.services.StatusServiceV3;
import sit.int221.itbkkbackend.v3.services.TaskServiceV3;
import sit.int221.itbkkbackend.v3.services.BoardServiceV3;

import java.util.ArrayList;
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
        "http://intproj23.sit.kmutt.ac.th"
})
@RestController
@RequestMapping("/v3/boards")
public class BoardControllerV3 {
    //Board
    @Autowired
    private BoardServiceV3 boardService;
    @Autowired
    private ModelMapper mapper;

    @GetMapping("")
    public List<BoardDTO> getAllBoards() {
        return boardService.findAllBoards();
    }

    @GetMapping("/{id}")
    public BoardDTO getBoard(@PathVariable String id) {
        return mapper.map(boardService.findById(id), BoardDTO.class);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public BoardDTO addBoard(@RequestBody BoardDTO board) {
        return boardService.addBoard(board);
    }

    @PatchMapping("/{id}/maximum-task")
    public BoardDTO updateBoardMaximumTasks(@PathVariable String id, @RequestBody Map<String, Optional<Object>> board) {
        return boardService.updateBoardById(id, board);
    }

    //Task
    @Autowired
    private TaskServiceV3 taskService;

    @GetMapping("/{boardId}/tasks")
    public List<SimpleTaskDTO> getAllTasks(@RequestParam(defaultValue = "createdOn") String sortBy ,
                                           @RequestParam(defaultValue = "ASC") String sortDirection,
                                           @RequestParam(required = false) ArrayList<String> filterStatuses,
                                           @PathVariable String boardId){
        return taskService.getAllSimpleTasksDTO(sortBy,sortDirection,filterStatuses,boardId);
    }

    @GetMapping("/{boardId}/tasks/{id}")
    public TaskV3 getTask(@PathVariable Integer id,@PathVariable String boardId){
        return taskService.getTaskById(id,boardId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{boardId}/tasks")
    public TaskDTO addTask(@RequestBody TaskDTO task,@PathVariable String boardId){
        return taskService.addTask(task,boardId);
    }

    @DeleteMapping("/{boardId}/tasks/{id}")
    public SimpleTaskDTO deleteTask(@PathVariable Integer id,@PathVariable String boardId){
        return taskService.deleteTaskById(id,boardId);
    }

    @PutMapping("/{boardId}/tasks/{id}")
    public TaskDTO updateTask(@PathVariable Integer id , @RequestBody TaskDTO task,@PathVariable String boardId){
        return taskService.updateTaskById(id,task,boardId);
    }

    //Statuses

    @Autowired
    private StatusServiceV3 service;

    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<Object> getAllStatus(@PathVariable String boardId){
        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(service.getAllStatus(boardId)) ;
    }

    @GetMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<Object> getStatus(@PathVariable Integer id ,@PathVariable String boardId){
        return ResponseEntity.status(HttpStatus.OK).body(service.getStatusById(id,boardId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{boardId}/statuses")
    public StatusV3 addStatus(@RequestBody StatusDTO status,@PathVariable String boardId){
        return service.addStatus(status,boardId);
    }

    @PutMapping("/{boardId}/statuses/{id}")
    public StatusV3 updateStatus(@PathVariable Integer id , @RequestBody StatusDTO status,@PathVariable String boardId){
        return service.updateStatusById(id,status,boardId);
    }

    @DeleteMapping("/{boardId}/statuses/{id}")
    public StatusV3 deleteStatus(@PathVariable Integer id,@PathVariable String boardId){
        return service.deleteStatusById(id,boardId);
    }

    @DeleteMapping("/{boardId}/statuses/{oldId}/{newId}")
    public StatusV3 transferAndDeleteStatus(@PathVariable Integer oldId, @PathVariable Integer newId,@PathVariable String boardId){
        return service.transferAndDeleteStatus(oldId,newId,boardId);
    }
}

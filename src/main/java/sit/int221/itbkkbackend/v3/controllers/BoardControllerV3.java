package sit.int221.itbkkbackend.v3.controllers;


import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sit.int221.itbkkbackend.v3.dtos.*;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3;
import sit.int221.itbkkbackend.v3.entities.BoardV3;
import sit.int221.itbkkbackend.v3.entities.StatusV3;
import sit.int221.itbkkbackend.v3.entities.TaskV3;
import sit.int221.itbkkbackend.v3.repositories.BoardPermissionRepositoryV3;
import sit.int221.itbkkbackend.v3.services.BoardPermissionServiceV3;
import sit.int221.itbkkbackend.v3.services.StatusServiceV3;
import sit.int221.itbkkbackend.v3.services.TaskServiceV3;
import sit.int221.itbkkbackend.v3.services.BoardServiceV3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
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
},allowCredentials = "true")
@RestController
@RequestMapping("/v3/boards")
public class BoardControllerV3 {
    //Board
    @Autowired
    private BoardServiceV3 boardService;

    @GetMapping("")
    public BoardListDTO getAllBoards() {
        return boardService.findAllBoards();
    }

    @GetMapping("/{id}")
    public BoardDTO getBoard(@PathVariable String id) {
        return boardService.findByIdAndOwnerId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public BoardDTO addBoard(@RequestBody BoardDTO board) {
        return boardService.addBoard(board);
    }

    @PatchMapping("/{id}")
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
    public TaskDetailsDTO getTask(@PathVariable Integer id,@PathVariable String boardId){
        return taskService.getTaskById(id,boardId);
    }

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{boardId}/tasks")
    public TaskDTO addTask(@RequestBody(required = false) TaskDTO task,@PathVariable String boardId){
        if (task == null) task = new TaskDTO();
        return taskService.addTask(task,boardId);
    }

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @DeleteMapping("/{boardId}/tasks/{id}")
    public SimpleTaskDTO deleteTask(@PathVariable Integer id,@PathVariable String boardId){
        return taskService.deleteTaskById(id,boardId);
    }

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @PutMapping("/{boardId}/tasks/{id}")
    public TaskDTO updateTask(@PathVariable Integer id , @RequestBody(required = false) TaskDTO task,@PathVariable String boardId){
        if (task == null) task = new TaskDTO();
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

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{boardId}/statuses")
    public StatusV3 addStatus(@RequestBody(required = false) StatusDTO status,@PathVariable String boardId){
        if (status == null) status = new StatusDTO();
        return service.addStatus(status,boardId);
    }

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @PutMapping("/{boardId}/statuses/{id}")
    public StatusV3 updateStatus(@PathVariable Integer id , @RequestBody(required = false) StatusDTO status,@PathVariable String boardId){
        if (status == null) status = new StatusDTO();
        return service.updateStatusById(id,status,boardId);
    }

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @DeleteMapping("/{boardId}/statuses/{id}")
    public StatusV3 deleteStatus(@PathVariable Integer id,@PathVariable String boardId){
        return service.deleteStatusById(id,boardId);
    }

    @PreAuthorize("hasAnyAuthority('WRITE','OWNER')")
    @DeleteMapping("/{boardId}/statuses/{oldId}/{newId}")
    public StatusV3 transferAndDeleteStatus(@PathVariable Integer oldId, @PathVariable Integer newId,@PathVariable String boardId){
        return service.transferAndDeleteStatus(oldId,newId,boardId);
    }

    @Autowired
    BoardPermissionServiceV3 boardPermissionService;

    // Collaborators
    @GetMapping("/{boardId}/collabs")
    public List<CollaboratorDTO> getAllCollaborators(@PathVariable String boardId){
        return boardPermissionService.findAllCollaborator(boardId);
    }

    // need to be fix change to query based instead
    @PreAuthorize("hasAnyAuthority('OWNER','COLLABORATOR') or #oid == authentication.principal.oid")
    @GetMapping("/{boardId}/collabs/{oid}")
    public CollaboratorDetailsDTO getCollaborator(@PathVariable String boardId, @PathVariable String oid){
        return boardPermissionService.findCollaboratorByOid(boardId,oid);
    }
    @PreAuthorize("hasAuthority('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{boardId}/collabs")
    public CollaboratorDTO addCollaborator(@PathVariable String boardId, @RequestBody AddCollaboratorDTO collaborator){
        return boardPermissionService.addPermissionOnBoard(boardId,collaborator);
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PatchMapping("/{boardId}/collabs/{oid}")
    public UpdateCollaboratorDTO updateCollaborator(@PathVariable String boardId, @PathVariable String oid, @RequestBody UpdateCollaboratorDTO collaborator){
        return  boardPermissionService.updateAccessRight(boardId,oid,collaborator);
    }

    @PreAuthorize("#oid == authentication.principal.oid")
    @PatchMapping("/{boardId}/collabs/{oid}/accept")
    public void updateCollaboratorInvite(@PathVariable String boardId, @PathVariable String oid){
        boardPermissionService.updateInviteStatus(boardId,oid);
    }


    @PreAuthorize("hasAuthority('OWNER') or #oid == authentication.principal.oid")
    @DeleteMapping("/{boardId}/collabs/{oid}")
    public void removeCollaborator(@PathVariable String boardId, @PathVariable String oid){
        boardPermissionService.removeAccessRight(boardId,oid);
    }


}

package sit.int221.itbkkbackend.v2.services;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.exceptions.ItemNotFoundException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.SaveTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.entities.TaskV2;
import sit.int221.itbkkbackend.v2.repositories.TaskRepositoryV2;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskServiceV2 {
    @Autowired
    private TaskRepositoryV2 taskRepository;
    @Autowired
    private StatusServiceV1 statusService;
    @Autowired
    private BoardServiceV2 boardService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    ValidatingServiceV2 validatingService;

    private TaskV2 findById(Integer id){
        return taskRepository.findById(id).orElseThrow(()-> new ItemNotFoundException(
                HttpStatus.NOT_FOUND,id
        ));
    }
    public List<SimpleTaskDTO> getAllSimpleTasksDTO(String sortBy,String sortDirection, ArrayList<String> filterStatuses,Integer boardId){
        try{
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection),sortBy);
            List<TaskV2> taskV2List = boardId == null ? taskRepository.findAll(sort) : taskRepository.findAllByBoardId(boardId,sort) ;
            List<TaskV2> filteredTaskList = filterStatuses == null || filterStatuses.size() == 0 ? taskV2List : taskV2List.stream().filter(taskV2 -> filterStatuses.contains(taskV2.getStatus().getName())).toList();
            return listMapper.mapList(filteredTaskList, SimpleTaskDTO.class,mapper);
        }catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public TaskV2 getTaskById(Integer id){
        return findById(id) ;
    }

    @Transactional
    public TaskDTO addTask(SaveTaskDTO task){
        validatingService.validateSaveTaskDTO(task);
        TaskV2 validatedTask = mapper.map(task, TaskV2.class);
        StatusV2 taskStatus = statusService.findById(task.getStatus() == null ? task.getStatusId() : task.getStatus());
        BoardV2 currentBoard = boardService.findById(task.getBoardId());
        if(taskStatus.getTasks().size() + 1 > currentBoard.getTaskLimitPerStatus() && currentBoard.getIsLimitTasks()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",taskStatus.getName()));
        }
        validatedTask.setStatus(taskStatus);
        validatedTask.setId(null);
        validatedTask.setBoard(currentBoard);

        return mapper.map(taskRepository.save(validatedTask),TaskDTO.class);

    }

    @Transactional
    public SimpleTaskDTO deleteTaskById(Integer id){
        TaskV2 foundedTask = taskRepository.findById(id).orElseThrow(()-> new DeleteItemNotFoundException(
                HttpStatus.NOT_FOUND
        ));
        taskRepository.delete(findById(id));
        return mapper.map(foundedTask,SimpleTaskDTO.class);
    }

    @Transactional
    public TaskDTO updateTaskById(Integer id, TaskDTO task){
        findById(id);
        task.setId(id);
        validatingService.validateTaskDTO(task);
        TaskV2 validatedUpdateTask = mapper.map(task, TaskV2.class);
        StatusV2 taskStatus = statusService.findById(task.getStatusId());
        BoardV2 currentBoard = boardService.findById(task.getBoardId());
        if(taskStatus.getTasks().size() + 1 > currentBoard.getTaskLimitPerStatus() && currentBoard.getIsLimitTasks()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",taskStatus.getName()));
        }
        validatedUpdateTask.setStatus(taskStatus);
        validatedUpdateTask.setBoard(currentBoard);
        return mapper.map(taskRepository.save(validatedUpdateTask),TaskDTO.class);
    }




}
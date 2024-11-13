package sit.int221.itbkkbackend.v2.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.CustomConstraintViolationException;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.exceptions.ItemNotFoundException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.entities.TaskV2;
import sit.int221.itbkkbackend.v2.repositories.TaskRepositoryV2;

import java.util.List;

@Service
public class TaskServiceV2 {
    private final TaskRepositoryV2 taskRepository;
    private final StatusServiceV2 statusService;
    private final BoardServiceV2 boardService;
    private final ValidatingServiceV2 validatingService;
    private final ModelMapper mapper;
    private final ListMapper listMapper;

    public TaskServiceV2(TaskRepositoryV2 taskRepository, StatusServiceV2 statusService, BoardServiceV2 boardService, ValidatingServiceV2 validatingService, ModelMapper mapper, ListMapper listMapper) {
        this.taskRepository = taskRepository;
        this.statusService = statusService;
        this.boardService = boardService;
        this.validatingService = validatingService;
        this.mapper = mapper;
        this.listMapper = listMapper;
    }

    private TaskV2 findById(Integer id){
        return taskRepository.findById(id).orElseThrow(()-> new ItemNotFoundException(
                HttpStatus.NOT_FOUND,id
        ));
    }
    public List<SimpleTaskDTO> getAllSimpleTasksDTO(String sortBy,String sortDirection, List<String> filterStatuses,Integer boardId){
        try{
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection),sortBy);
            if(sortBy.equals("createdOn")){
                sort = sort.and(Sort.by(Sort.Direction.ASC,"id"));
            }
            List<TaskV2> taskV2List = boardId == null ? taskRepository.findAll(sort) : taskRepository.findAllByBoardId(boardId,sort) ;
            List<TaskV2> filteredTaskList = filterStatuses == null || filterStatuses.isEmpty() ? taskV2List : taskV2List.stream().filter(taskV2 -> filterStatuses.contains(taskV2.getStatus().getName())).toList();
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
    public TaskDTO addTask(TaskDTO task){
        validateTaskDTOField(task);
        task.setId(null);
        TaskV2 validatedTask = initializeTask(task);
        return mapper.map(taskRepository.save(validatedTask),TaskDTO.class);
    }

    @Transactional
    public SimpleTaskDTO deleteTaskById(Integer id){
        TaskV2 foundedTask = taskRepository.findById(id).orElseThrow(
                ()-> new DeleteItemNotFoundException(HttpStatus.NOT_FOUND
                ));
        taskRepository.delete(findById(id));
        return mapper.map(foundedTask,SimpleTaskDTO.class);
    }

    @Transactional
    public TaskDTO updateTaskById(Integer id, TaskDTO task){
        findById(id);
        validateTaskDTOField(task);
        task.setId(id);
        TaskV2 validatedTask = initializeTask(task);
        return mapper.map(taskRepository.save(validatedTask),TaskDTO.class);
    }

    public void validateTaskDTOField(TaskDTO task){
        Boolean isStatusExist = statusService.isExist(task.getStatusId());
        try{
            validatingService.validateTaskDTO(task,isStatusExist);
        }catch (ConstraintViolationException exception){
            CustomConstraintViolationException taskConstraintViolationException = new CustomConstraintViolationException(exception.getConstraintViolations());
            if (!isStatusExist.booleanValue()){
                taskConstraintViolationException.getAdditionalErrorFields().put("status","does not exist");
            }
            throw taskConstraintViolationException;
        }
    }

    public TaskV2 initializeTask(TaskDTO task){
        TaskV2 validatedTask = mapper.map(task, TaskV2.class);
        StatusV2 taskStatus = statusService.findById(task.getStatusId());
        BoardV2 currentBoard = boardService.findById(task.getBoardId());
        Integer taskAmount = taskRepository.countByStatusId(task.getStatusId());
        Boolean isExceedLimit;
        if(task.getId() == null || !task.getStatusId().equals(findById(task.getId()).getStatusId())){
            isExceedLimit = taskAmount + 1 > currentBoard.getTaskLimitPerStatus();
        }else {
            isExceedLimit = false;
        }
        if(!taskStatus.getIsPredefined().booleanValue() &&
                currentBoard.getIsTaskLimitEnabled().booleanValue() &&
                isExceedLimit.booleanValue()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",taskStatus.getName()));
        }
        validatedTask.setStatus(taskStatus);
        validatedTask.setBoard(currentBoard);
        return validatedTask;
    }
}

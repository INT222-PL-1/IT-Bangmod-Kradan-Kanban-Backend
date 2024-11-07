package sit.int221.itbkkbackend.v3.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.CustomConstraintViolationException;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.exceptions.ItemNotFoundException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v3.dtos.TaskDTO;
import sit.int221.itbkkbackend.v3.entities.StatusV3;
import sit.int221.itbkkbackend.v3.entities.TaskV3;
import sit.int221.itbkkbackend.v3.repositories.FileRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.TaskRepositoryV3;
import sit.int221.itbkkbackend.v3.entities.BoardV3;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class TaskServiceV3 {
    @Autowired
    private TaskRepositoryV3 taskRepository;
    @Autowired
    private StatusServiceV3 statusService;
    @Autowired
    private BoardServiceV3 boardService;
    @Autowired
    ValidatingServiceV3 validatingService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private FileSystemStorageService fileService;


    private TaskV3 findById(Integer id){
        return taskRepository.findById(id).orElseThrow(()->
                new ItemNotFoundException(HttpStatus.NOT_FOUND,id)
        );
    }

    public void validateTaskDTOField(TaskDTO task){
        Boolean isStatusExist = statusService.isExist(task.getStatusId());
        try{
            validatingService.validateTaskDTO(task,isStatusExist);
        }catch (ConstraintViolationException exception){
            CustomConstraintViolationException taskConstraintViolationException = new CustomConstraintViolationException(exception.getConstraintViolations());
            if (!isStatusExist){
                taskConstraintViolationException.getAdditionalErrorFields().put("status","does not exist");
            }
            taskConstraintViolationException.setRootEntityName("TaskV3");
            throw taskConstraintViolationException;
        }
    }

    public TaskV3 initializeTask(TaskDTO task){
        TaskV3 validatedTask = mapper.map(task, TaskV3.class);
        StatusV3 taskStatus = statusService.findByIdAndBoardId(task.getStatusId(), task.getBoardId());
        BoardV3 currentBoard = boardService.findById(task.getBoardId());
        Integer taskAmount = taskRepository.countByStatusIdAndBoardId(task.getStatusId(), task.getBoardId());
        log.info(task.getBoardId());
        log.info(task.getStatusId().toString());
        log.info(taskAmount.toString());
        Boolean isExceedLimit;
        if(task.getId() == null || task.getStatusId() != findById(task.getId()).getStatusId()){
            isExceedLimit = taskAmount + 1 > currentBoard.getTaskLimitPerStatus();
        }else {
            isExceedLimit = false;
        }
        if(!taskStatus.getIsPredefined() &&
                currentBoard.getIsTaskLimitEnabled() &&
                isExceedLimit
        ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",taskStatus.getName()));
        }
        validatedTask.setStatus(taskStatus);
        validatedTask.setBoard(currentBoard);
        return validatedTask;
    }

    // Controller Service Method [GET , POST , DELETE , PUT]

    public List<SimpleTaskDTO> getAllSimpleTasksDTO(String sortBy,String sortDirection, ArrayList<String> filterStatuses,String boardId){
        boardService.isExist(boardId);
        try{
            // create sort object , find all task in specific board with sort , filter tasks
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection),sortBy);
            if(sortBy.equals("createdOn")){
                sort = sort.and(Sort.by(Sort.Direction.ASC,"id"));
            }
            List<TaskV3> taskV3List = taskRepository.findAllByBoardId(boardId,sort) ;
            List<TaskV3> filteredTaskList = filterStatuses == null || filterStatuses.size() == 0 ? taskV3List : taskV3List.stream().filter(taskV3 -> filterStatuses.contains(taskV3.getStatus().getName())).toList();
            return listMapper.mapList(filteredTaskList, SimpleTaskDTO.class,mapper);
        }catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    public TaskV3 getTaskById(Integer id,String boardId){
        boardService.isExist(boardId);
        TaskV3 task =  taskRepository.findByIdAndBoardId(id,boardId) ;
        if(task == null){
            throw new ItemNotFoundException(HttpStatus.NOT_FOUND,id);
        }
        return task;
    }

    @Transactional
    public TaskDTO addTask(TaskDTO task,String boardId){
        boardService.isExist(boardId);
        validateTaskDTOField(task);
        task.setId(null);
        task.setBoardId(boardId);
        TaskV3 validatedTask = initializeTask(task);
        return mapper.map(taskRepository.save(validatedTask),TaskDTO.class);
    }

    @Transactional
    public SimpleTaskDTO deleteTaskById(Integer id,String boardId){
        boardService.isExist(boardId);
        TaskV3 task =  taskRepository.findByIdAndBoardId(id,boardId) ;
        if(task == null){
            throw new DeleteItemNotFoundException(HttpStatus.NOT_FOUND);
        }
        taskRepository.delete(task);
        return mapper.map(task,SimpleTaskDTO.class);
    }

    @Transactional
    public TaskDTO updateTaskById(Integer id, TaskDTO task,String boardId){
        boardService.isExist(boardId);
        TaskV3 foundedTask =  taskRepository.findByIdAndBoardId(id,boardId);
        if(foundedTask == null){
            throw new ItemNotFoundException(HttpStatus.NOT_FOUND,id);
        }
        validateTaskDTOField(task);
        fileService.deleteFilesExcept(id,task.getAttachments());
        task.setBoardId(boardId);
        task.setId(id);
        TaskV3 validatedTask = initializeTask(task);
        return mapper.map(taskRepository.save(validatedTask),TaskDTO.class);
    }
}

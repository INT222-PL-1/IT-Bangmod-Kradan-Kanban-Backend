package sit.int221.itbkkbackend.v1.services;


import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v1.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v1.dtos.TaskDTO;
import sit.int221.itbkkbackend.v1.entities.StatusV1;
import sit.int221.itbkkbackend.v1.entities.TaskV1;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.exceptions.ItemNotFoundException;
import sit.int221.itbkkbackend.v1.repositories.TaskRepositoryV1;


import java.lang.reflect.Field;
import java.util.*;

@Service
public class TaskServiceV1 {
    private final TaskRepositoryV1 repository;
    private final ModelMapper mapper;
    private final ListMapper listMapper;
    private final ValidatingServiceV1 validatingService;

    public TaskServiceV1(TaskRepositoryV1 repository, ModelMapper mapper, ListMapper listMapper, ValidatingServiceV1 validatingService) {
        this.repository = repository;
        this.mapper = mapper;
        this.listMapper = listMapper;
        this.validatingService = validatingService;
    }

    private TaskV1 findById(Integer id){
        return repository.findById(id).orElseThrow(()-> new ItemNotFoundException(
                HttpStatus.NOT_FOUND,id
        ));
    }
    public List<SimpleTaskDTO> getAllSimpleTasksDTO(){
        return listMapper.mapList(repository.findAll(Sort.by("createdOn").ascending()), SimpleTaskDTO.class,mapper);
    }

    public TaskDTO getTaskDTOById(Integer id){
        return mapper.map(findById(id),TaskDTO.class) ;
    }

    @Transactional
    public TaskV1 addTask(TaskDTO task){
        task.setId(null);
        TaskV1 validateTask = mapper.map(task, TaskV1.class);
        try {
            validatingService.validateTaskDTO(mapper.map(validateTask,TaskDTO.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return repository.save(validateTask);
    }

    @Transactional
    public SimpleTaskDTO deleteTaskById(Integer id){
        TaskV1 foundedTask = repository.findById(id).orElseThrow(()-> new DeleteItemNotFoundException(
                HttpStatus.NOT_FOUND
        ));
        repository.delete(findById(id));
        return mapper.map(foundedTask,SimpleTaskDTO.class);
    }

    @Transactional
    public TaskV1 updateTaskById(Integer id, TaskV1 task){
        findById(id);
        task.setId(id);
        TaskDTO validatedUpdateTask =  mapper.map(task,TaskDTO.class);
        try {
            validatingService.validateTaskDTO(validatedUpdateTask);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return repository.save(mapper.map(validatedUpdateTask, TaskV1.class));
    }

    @Transactional
    public TaskV1 updatePartialTaskById(Integer id, Map<String, Optional<Object>> task){
        TaskV1 updateTask = findById(id);
        List<String> validUpdateInfo = new ArrayList<>(Arrays.asList("title","description", "assignees",  "status")).stream().filter(task::containsKey).toList();
        for (String attribute : validUpdateInfo){
            Object value = task.get(attribute).isPresent() ?  task.get(attribute).get() : null;
            if (attribute.equals("status")){
                assert value != null;
                value = StatusV1.valueOf(value.toString());
            }
            try {
                Field updateInfo = TaskV1.class.getDeclaredField(attribute);
                updateInfo.setAccessible(true);
                updateInfo.set(updateTask, value);
                updateInfo.setAccessible(false);
            }
            catch(Exception exception){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        TaskDTO validatedUpdateTask =  mapper.map(updateTask,TaskDTO.class);
        try {
            validatingService.validateTaskDTO(validatedUpdateTask);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return repository.save(updateTask);

    }


}

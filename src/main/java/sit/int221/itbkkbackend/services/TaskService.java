package sit.int221.itbkkbackend.services;


import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.dtos.TaskDTO;
import sit.int221.itbkkbackend.entities.Status;
import sit.int221.itbkkbackend.entities.Task;
import sit.int221.itbkkbackend.exceptions.ItemNotFoundException;
import sit.int221.itbkkbackend.repositories.TaskRepository;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    ValidatingService validatingService;

    private Task findById(Integer id){
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
    public Task addTask(TaskDTO task){
        task.setId(null);
        return repository.save(mapper.map(task,Task.class));
    }

    @Transactional
    public SimpleTaskDTO deleteTaskById(Integer id){
        Task foundedTask = findById(id);
        repository.delete(findById(id));
        return mapper.map(foundedTask,SimpleTaskDTO.class);
    }

    @Transactional
    public Task updateTaskById(Integer id, Task task){
        findById(id);
        task.setId(id);
        TaskDTO validatedUpdateTask =  mapper.map(task,TaskDTO.class);
        try {
            validatingService.validateTaskDTO(validatedUpdateTask);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return repository.save(mapper.map(validatedUpdateTask,Task.class));
    }

    @Transactional
    public Task updatePartialTaskById(Integer id, Map<String, Optional<Object>> task){
        Task updateTask = findById(id);
        List<String> validUpdateInfo = new ArrayList<>(Arrays.asList("title","description", "assignees",  "status")).stream().filter(task::containsKey).toList();
        for (String attribute : validUpdateInfo){
            Object value = task.get(attribute).isPresent() ?  task.get(attribute).get() : null;
            if (attribute.equals("status")){
                assert value != null;
                value = Status.valueOf(value.toString());
            }
            try {
                Field updateInfo = Task.class.getDeclaredField(attribute);
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

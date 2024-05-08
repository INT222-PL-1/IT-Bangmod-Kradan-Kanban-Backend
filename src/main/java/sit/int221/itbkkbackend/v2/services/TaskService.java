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
import sit.int221.itbkkbackend.v2.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v2.dtos.TaskDTO;
import sit.int221.itbkkbackend.v2.entities.Status;
import sit.int221.itbkkbackend.v2.entities.Task;
import sit.int221.itbkkbackend.v2.repositories.TaskRepository;

import java.util.List;

@Slf4j
@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusService statusService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    ValidatingService validatingService;

    private Task findById(Integer id){
        return taskRepository.findById(id).orElseThrow(()-> new ItemNotFoundException(
                HttpStatus.NOT_FOUND,id
        ));
    }
    public List<SimpleTaskDTO> getAllSimpleTasksDTO(){
        return listMapper.mapList(taskRepository.findAll(Sort.by("createdOn").ascending()), SimpleTaskDTO.class,mapper);
    }

    public Task getTaskById(Integer id){
        return mapper.map(findById(id),Task.class) ;
    }

    @Transactional
    public TaskDTO addTask(TaskDTO task){
        try{
            validatingService.validateTaskDTO(task);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Task validatedTask = mapper.map(task,Task.class);
        Status taskStatus = statusService.findById(task.getStatusId());
        validatedTask.setStatus(taskStatus);
        validatedTask.setId(null);

        return mapper.map(taskRepository.save(validatedTask),TaskDTO.class);

    }

    @Transactional
    public SimpleTaskDTO deleteTaskById(Integer id){
        Task foundedTask = taskRepository.findById(id).orElseThrow(()-> new DeleteItemNotFoundException(
                HttpStatus.NOT_FOUND
        ));
        taskRepository.delete(findById(id));
        return mapper.map(foundedTask,SimpleTaskDTO.class);
    }

    @Transactional
    public TaskDTO updateTaskById(Integer id, TaskDTO task){
        findById(id);
        task.setId(id);
        try{
            validatingService.validateTaskDTO(task);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Task validatedUpdateTask = mapper.map(task,Task.class);
        Status taskStatus = statusService.findById(task.getStatusId());
        validatedUpdateTask.setStatus(taskStatus);
        return mapper.map(taskRepository.save(validatedUpdateTask),TaskDTO.class);

    }

//    @Transactional
//    public Task updatePartialTaskById(Integer id, Map<String, Optional<Object>> task){
//        Task updateTask = findById(id);
//        List<String> validUpdateInfo = new ArrayList<>(Arrays.asList("title","description", "assignees",  "status")).stream().filter(task::containsKey).toList();
//        for (String attribute : validUpdateInfo){
//            Object value = task.get(attribute).isPresent() ?  task.get(attribute).get() : null;
//            if (attribute.equals("status")){
//                assert value != null;
//                value = Status.valueOf(value.toString());
//            }
//            try {
//                Field updateInfo = Task.class.getDeclaredField(attribute);
//                updateInfo.setAccessible(true);
//                updateInfo.set(updateTask, value);
//                updateInfo.setAccessible(false);
//            }
//            catch(Exception exception){
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//            }
//        }
//        TaskDTO validatedUpdateTask =  mapper.map(updateTask,TaskDTO.class);
//        try {
//            validatingService.validateTaskDTO(validatedUpdateTask);
//        } catch (Exception e){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//        return taskRepository.save(updateTask);
//        return null;
//
//    }


}

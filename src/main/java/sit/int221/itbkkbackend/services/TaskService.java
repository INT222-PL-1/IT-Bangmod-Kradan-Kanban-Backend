package sit.int221.itbkkbackend.services;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.dtos.TaskDTO;
import sit.int221.itbkkbackend.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ListMapper listMapper;

    public List<SimpleTaskDTO> getAllTasks(){
        return listMapper.mapList(repository.findAll(Sort.by("createdOn").ascending()), SimpleTaskDTO.class,mapper);
    }

    public TaskDTO getTaskById(Integer id){
        return mapper.map(repository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("Task id %d does not exist !!!",id))),TaskDTO.class) ;
    }


}

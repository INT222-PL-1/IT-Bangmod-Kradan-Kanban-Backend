package sit.int221.itbkkbackend.v2.services;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.Status;
import sit.int221.itbkkbackend.v2.repositories.StatusRepository;
import sit.int221.itbkkbackend.v2.repositories.TaskRepository;

import java.util.List;

@Slf4j
@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper mapper;

    public Status findByName(String name) {
        return statusRepository.findByName(name);
    }

    public Status findById(Integer id){
        return statusRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public List<?> findAllStatus(Boolean count){
        return count ?  listMapper.mapList( statusRepository.findAll(), StatusDTO.class,mapper) : statusRepository.findAll();
    }

    @Transactional
    public Status addStatus(StatusDTO status){
        status.setId(null);
        try {
            return statusRepository.save(mapper.map(status,Status.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    @Transactional
    public Status editStatus(Integer id, StatusDTO status){
        Status foundedStatus = findById(id);
        status.setId(id);
        try {
            return statusRepository.save(mapper.map(status,Status.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    public void transferTasksStatus(Status oldStatus,Status newStatus){
        taskRepository.updateAllStatusByStatus(oldStatus,newStatus);
    }

    @Transactional
    public Status deleteStatus(Integer id){
        Status oldStatus = statusRepository.findById(id).orElseThrow(()-> new DeleteItemNotFoundException(HttpStatus.NOT_FOUND));
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

    @Transactional
    public Status transferAndDeleteStatus(Integer oldId,Integer newId){
        Status oldStatus = findById(oldId);
        Status newStatus = findById(newId);
        transferTasksStatus(oldStatus,newStatus);
        statusRepository.delete(oldStatus);
        return oldStatus;


    }
}

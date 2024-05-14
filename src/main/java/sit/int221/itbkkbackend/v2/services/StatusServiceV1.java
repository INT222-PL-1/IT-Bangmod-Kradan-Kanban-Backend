package sit.int221.itbkkbackend.v2.services;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.exceptions.DuplicateStatusNameException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.repositories.StatusRepositoryV1;
import sit.int221.itbkkbackend.v2.repositories.TaskRepositoryV2;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class StatusServiceV1 {
    @Autowired
    private StatusRepositoryV1 statusRepository;
    @Autowired
    private TaskRepositoryV2 taskRepository;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper mapper;

    public StatusV2 findByName(String name) {
        return statusRepository.findByName(name);
    }

    public StatusV2 findById(Integer id){
        return statusRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public List<?> getAllStatus(Boolean count){
        return count ?  listMapper.mapList( statusRepository.findAll(), StatusDTO.class,mapper) : statusRepository.findAll();
    }

    public Object getStatusById(Integer id , Boolean count){
        return count ? mapper.map(findById(id),StatusDTO.class) : findById(id);
    }

    @Transactional
    public StatusV2 addStatus(StatusDTO status){
        status.setId(null);
        if (findByName(status.getName()) != null){
            throw new DuplicateStatusNameException(HttpStatus.BAD_REQUEST,status.getName());
        }
        return statusRepository.save(mapper.map(status, StatusV2.class));

    }

    @Transactional
    public StatusV2 updateStatusById(Integer id, StatusDTO status){
        StatusV2 foundedStatus = findById(id);
        if (foundedStatus.getIs_fixed_status() == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't delete default status.");
        }
        StatusV2 duplicateStatus = findByName(status.getName());
        if (duplicateStatus != null && !Objects.equals(duplicateStatus.getId(), id)){
            throw new DuplicateStatusNameException(HttpStatus.BAD_REQUEST,status.getName());
        }
        status.setId(id);
        return statusRepository.save(mapper.map(status, StatusV2.class));

    }

    public void transferTasksStatus(StatusV2 oldStatus, StatusV2 newStatus){
        taskRepository.updateAllStatusByStatus(oldStatus,newStatus);
    }

    @Transactional
    public StatusV2 deleteStatusById(Integer id){
        StatusV2 oldStatus = statusRepository.findById(id).orElseThrow(()-> new DeleteItemNotFoundException(HttpStatus.NOT_FOUND));
        if (oldStatus.getIs_fixed_status() == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't delete default status.");
        }
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

    @Transactional
    public StatusV2 transferAndDeleteStatus(Integer oldId, Integer newId){
        StatusV2 oldStatus = findById(oldId);
        if (oldStatus.getIs_fixed_status() == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't delete default status.");
        }
        StatusV2 newStatus = findById(newId);
        transferTasksStatus(oldStatus,newStatus);
        statusRepository.delete(oldStatus);
        return oldStatus;


    }
}
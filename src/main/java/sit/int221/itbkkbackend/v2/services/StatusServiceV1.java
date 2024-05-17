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
import sit.int221.itbkkbackend.v2.dtos.SetStatusMaxTasksDTO;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
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
    private BoardServiceV2 boardServiceV2;
    @Autowired
    private ValidatingServiceV2 validatingService;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ModelMapper mapper;

    public StatusV2 findByName(String name) {
        return statusRepository.findByName(name);
    }

    public StatusV2 findById(Integer id){
        return statusRepository.findById(id == null ? findByName("No Status").getId() : id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public List<?> getAllStatus(Boolean count){
        return count ?  listMapper.mapList( statusRepository.findAll(), StatusDTO.class,mapper) : statusRepository.findAll();
    }

    public Object getStatusById(Integer id , Boolean count){
        return count ? mapper.map(findById(id),StatusDTO.class) : findById(id);
    }

    @Transactional
    public StatusV2 addStatus(StatusDTO status){
        validatingService.validateStatusDTO(status);
        status.setId(null);
        if (findByName(status.getName()) != null){
            throw new DuplicateStatusNameException(HttpStatus.BAD_REQUEST,status.getName());
        }
        return statusRepository.save(mapper.map(status, StatusV2.class));
    }

    @Transactional
    public StatusV2 updateStatusById(Integer id, StatusDTO status){
        StatusV2 foundedStatus = findById(id);
        validatingService.validateStatusDTO(status);
        if (foundedStatus.getIs_fixed_status()){
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
        if (oldStatus.getIs_fixed_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't delete default status.");
        }
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

    @Transactional
    public StatusV2 transferAndDeleteStatus(Integer oldId, Integer newId){
        if (Objects.equals(oldId, newId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to transfer a status that you want to delete.");
        }
        StatusV2 oldStatus = findById(oldId);
        StatusV2 newStatus = findById(newId);
        BoardV2 currentBoard = boardServiceV2.findById(1);
        if(newStatus.getTasks().size() + oldStatus.getTasks().size() > currentBoard.getTaskLimitPerStatus() && currentBoard.getIsLimitTasks()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",newStatus.getName()));
        }
        if (oldStatus.getTasks().size() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to transfer task. The current status does not contain any tasks.");
        }
        if (oldStatus.getIs_fixed_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Can't delete default status.");
        }

        transferTasksStatus(oldStatus,newStatus);
        statusRepository.delete(oldStatus);
        return oldStatus;


    }

//    @Transactional
//    public SetStatusMaxTasksDTO updateStatusMaxTasksById(Integer id,SetStatusMaxTasksDTO status){
//        StatusV2 updateStatus =  findById(id);
//        if(updateStatus.getTasks().size() <= updateStatus.getMaximum_limit()){
//          updateStatus.setIs_limited_status(status.getLimitMaximumTasks());
//            SetStatusMaxTasksDTO updatedStatus = mapper.map(updateStatus,SetStatusMaxTasksDTO.class);
//            updatedStatus.setTasks(null);
//            return updatedStatus;
//        }else{
//            return mapper.map(updateStatus,SetStatusMaxTasksDTO.class);
//        }
//        return mapper.map(updateStatus,SetStatusMaxTasksDTO.class);
//
//    }
}
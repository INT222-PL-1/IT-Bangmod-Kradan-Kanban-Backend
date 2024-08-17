package sit.int221.itbkkbackend.v2.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;
import sit.int221.itbkkbackend.exceptions.DuplicateStatusNameException;
import sit.int221.itbkkbackend.exceptions.CustomConstraintViolationException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.repositories.StatusRepositoryV2;
import sit.int221.itbkkbackend.v2.repositories.TaskRepositoryV2;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatusServiceV2 {
    @Autowired
    private StatusRepositoryV2 statusRepository;
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
        return statusRepository.findById(id == null ? findByName("No Status").getId() : id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"The status does not exist"));

    }
    public Boolean isExist(Integer id){
        try{
            return statusRepository.existsById(id);
        }catch (Exception e){
            return false;
        }
    }


    public List<StatusDTO> getAllStatus(Integer boardId){
        List <StatusDTO> statusList = listMapper.mapList( statusRepository.findAll(), StatusDTO.class,mapper);
//        if (boardId == null){return statusList;}
//        boardServiceV2.findById(boardId);
        statusList.forEach(status -> {
            status.setBoardId(boardId);
            status.setCount(taskRepository.countByStatusId(status.getId()));
        });
        return statusList;


    }

    public Object getStatusById(Integer id , Integer boardId){
        StatusDTO status = mapper.map(findById(id),StatusDTO.class);
//        if(boardId == null){return status;}
//        boardServiceV2.findById(boardId);
        status.setBoardId(boardId);
        status.setCount(taskRepository.countByStatusId(id));
        return status;
    }

    @Transactional
    public StatusV2 addStatus(StatusDTO status){
        status.setId(null);
        validateStatusDTOField(status);
        return statusRepository.save(mapper.map(status, StatusV2.class));
    }

    @Transactional
    public StatusV2 updateStatusById(Integer id, StatusDTO status){
        StatusV2 foundedStatus = findById(id);
        status.setId(id);
        validateStatusDTOField(status);
        if (foundedStatus.getIs_fixed_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be modified",foundedStatus.getName()));
        }
        return statusRepository.save(mapper.map(status, StatusV2.class));
    }

    public void transferTasksStatus(StatusV2 oldStatus, StatusV2 newStatus){
        taskRepository.updateAllStatusByStatus(oldStatus,newStatus);
    }

    @Transactional
    public StatusV2 deleteStatusById(Integer id){
        StatusV2 oldStatus = statusRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"The status does not exist"));
        Integer taskAmount = taskRepository.countByStatusId(id);
        if (oldStatus.getIs_fixed_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be deleted",oldStatus.getName()));
        }
        if(taskAmount > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be deleted. There are tasks currently associated with this status.",oldStatus.getName()));
        }
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

    @Transactional
    public StatusV2 transferAndDeleteStatus(Integer oldId, Integer newId){
        if (Objects.equals(oldId, newId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"destination status for task transfer must be different from current status");
        }
        StatusV2 oldStatus = statusRepository.findById(oldId).orElseThrow(()-> new  ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist"));
        StatusV2 newStatus = statusRepository.findById(newId).orElseThrow(()-> new  ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist"));
        BoardV2 currentBoard = boardServiceV2.findById(1);
        Integer oldStatusTaskAmount = taskRepository.countByStatusId(oldId);
        Integer newStatusTaskAmount = taskRepository.countByStatusId(newId);
        if(newStatusTaskAmount + oldStatusTaskAmount > currentBoard.getTaskLimitPerStatus() && currentBoard.getIsLimitTasks() && !newStatus.getIs_fixed_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",newStatus.getName()));
        }
        if (oldStatusTaskAmount == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to transfer task. The current status does not contain any tasks.");
        }
        if (oldStatus.getIs_fixed_status()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be deleted",oldStatus.getName()));
        }
        transferTasksStatus(oldStatus,newStatus);
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

    public void validateStatusDTOField(StatusDTO status){
        StatusV2 duplicateStatus = findByName(status.getName());
        Boolean isDuplicate = duplicateStatus != null && !Objects.equals(duplicateStatus.getId(), status.getId());
        try{
            validatingService.validateStatusDTO(status,isDuplicate);
        }catch (ConstraintViolationException exception){
            CustomConstraintViolationException customConstraintViolationException = new CustomConstraintViolationException(exception.getConstraintViolations());

            if (isDuplicate){
                customConstraintViolationException.getAdditionalErrorFields().put("name","must be unique");
            }
            throw customConstraintViolationException;
        }
    }

}
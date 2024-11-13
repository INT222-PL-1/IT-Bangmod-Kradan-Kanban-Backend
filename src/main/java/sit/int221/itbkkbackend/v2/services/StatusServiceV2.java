package sit.int221.itbkkbackend.v2.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.CustomConstraintViolationException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.repositories.StatusRepositoryV2;
import sit.int221.itbkkbackend.v2.repositories.TaskRepositoryV2;

import java.util.List;
import java.util.Objects;

@Service
public class StatusServiceV2 {
    private final StatusRepositoryV2 statusRepository;
    private final TaskRepositoryV2 taskRepository;
    private final BoardServiceV2 boardServiceV2;
    private final ValidatingServiceV2 validatingService;
    private final ListMapper listMapper;
    private final ModelMapper mapper;

    public StatusServiceV2(StatusRepositoryV2 statusRepository, TaskRepositoryV2 taskRepository, BoardServiceV2 boardServiceV2, ValidatingServiceV2 validatingService, ListMapper listMapper, ModelMapper mapper) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.boardServiceV2 = boardServiceV2;
        this.validatingService = validatingService;
        this.listMapper = listMapper;
        this.mapper = mapper;
    }

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
        statusList.forEach(status -> {
            status.setBoardId(boardId);
            status.setCount(taskRepository.countByStatusId(status.getId()));
        });
        return statusList;
    }

    public Object getStatusById(Integer id , Integer boardId){
        StatusDTO status = mapper.map(findById(id),StatusDTO.class);
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
        if (foundedStatus.getIsPredefined().booleanValue()) {
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
        if (oldStatus.getIsPredefined().booleanValue()) {
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
        if(newStatusTaskAmount + oldStatusTaskAmount > currentBoard.getTaskLimitPerStatus() && currentBoard.getIsTaskLimitEnabled().booleanValue() && !newStatus.getIsPredefined().booleanValue()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",newStatus.getName()));
        }
        if (oldStatusTaskAmount == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to transfer task. The current status does not contain any tasks.");
        }
        if (oldStatus.getIsPredefined().booleanValue()){
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

            if (isDuplicate.booleanValue()){
                customConstraintViolationException.getAdditionalErrorFields().put("name","must be unique");
            }
            throw customConstraintViolationException;
        }
    }

}
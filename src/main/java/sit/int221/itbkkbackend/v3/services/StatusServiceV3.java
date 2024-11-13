package sit.int221.itbkkbackend.v3.services;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.CustomConstraintViolationException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.StatusDTO;
import sit.int221.itbkkbackend.v3.entities.BoardV3;
import sit.int221.itbkkbackend.v3.entities.StatusV3;
import sit.int221.itbkkbackend.v3.repositories.StatusRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.TaskRepositoryV3;


import java.util.*;

@Slf4j
@Service
public class StatusServiceV3 {
    private final StatusRepositoryV3 statusRepository;
    private final TaskRepositoryV3 taskRepository;
    private final BoardServiceV3 boardServiceV3;
    private final ValidatingServiceV3 validatingService;
    private final ListMapper listMapper;
    private final ModelMapper mapper;

    public StatusServiceV3(StatusRepositoryV3 statusRepository, TaskRepositoryV3 taskRepository, BoardServiceV3 boardServiceV3, ValidatingServiceV3 validatingService, ListMapper listMapper, ModelMapper mapper) {
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.boardServiceV3 = boardServiceV3;
        this.validatingService = validatingService;
        this.listMapper = listMapper;
        this.mapper = mapper;
    }

    public Boolean isExist(Integer id){
        try{
            return statusRepository.existsById(id);
        }catch (Exception e){
            return false;
        }
    }

    public void transferTasksStatus(StatusV3 oldStatus, StatusV3 newStatus, String newStatusBoardId){
        taskRepository.updateAllStatusByStatusAndBoardId(oldStatus, newStatus, newStatusBoardId);
    }

    public StatusV3 findByIdAndBoardId(Integer id,String boardId){
        if(id == null){
            return statusRepository.findByName("No Status");
        }
        StatusV3 status = statusRepository.findById(id).orElseThrow(()->  new ResponseStatusException(HttpStatus.NOT_FOUND,"The status does not exist"));
        if(status.getBoardId() == null && !status.getIsPredefined().booleanValue()){
            checkDefaultStatusConfig(id,boardId);
        }
        if(status.getBoardId() != null && !Objects.equals(status.getBoardId(), boardId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The status does not exist");
        }
        return status;
    }

    public void checkDefaultStatusConfig (Integer statusId , String boardId){
        int configIndex = statusRepository.findRowIndexOfEditableDefaultStatusByStatusId(statusId);
        char[] config = boardServiceV3.findById(boardId).getDefaultStatusConfig().toCharArray();
        if(config[configIndex - 1] == '0'){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The status does not exist");
        }
    }

    public Boolean checkDuplicateStatusName(StatusDTO status){
        List<StatusV3> duplicateStatuses = statusRepository.findByNameAndBoardIdIsNotNull(status.getName(), status.getBoardId());
        StatusV3 duplicateStatus = duplicateStatuses.isEmpty() ? null :  duplicateStatuses.get(duplicateStatuses.size() - 1);
        // check if duplicate status is default status can be edit
        if(duplicateStatus != null && duplicateStatus.getBoardId() == null && !duplicateStatus.getIsPredefined().booleanValue()){
            int configIndex = statusRepository.findRowIndexOfEditableDefaultStatusByStatusId(duplicateStatus.getId());
            char[] config = boardServiceV3.findById(status.getBoardId()).getDefaultStatusConfig().toCharArray();
            if(config[configIndex - 1] == '0'){
                duplicateStatus = null;
            }
        }
        return duplicateStatus != null && !Objects.equals(duplicateStatus.getId(), status.getId());
    }

    private void disableDefaultStatus(String boardId, StatusV3 oldStatus) {
        Integer configIndex = statusRepository.findRowIndexOfEditableDefaultStatusByStatusId(oldStatus.getId());
        if (configIndex != null) configIndex = configIndex - 1;
        else return;
        char[] config = boardServiceV3.findById(boardId).getDefaultStatusConfig().toCharArray();
        config[configIndex] = '0';
        Map<String, Optional<Object>> updateDefaultStatusConfig = new HashMap<>();
        updateDefaultStatusConfig.put("defaultStatusConfig",Optional.of(String.valueOf(config)));
        boardServiceV3.updateBoardById(boardId, updateDefaultStatusConfig);
    }

    public void validateStatusDTOField(StatusDTO status) {
        Boolean isDuplicate = checkDuplicateStatusName(status);
        try {
            validatingService.validateStatusDTO(status, isDuplicate);
        } catch (ConstraintViolationException exception){
            CustomConstraintViolationException customConstraintViolationException = new CustomConstraintViolationException(exception.getConstraintViolations());

            if (isDuplicate.booleanValue()){
                customConstraintViolationException.getAdditionalErrorFields().put("name","must be unique");
            }
            customConstraintViolationException.setRootEntityName("StatusV3");
            throw customConstraintViolationException;
        }
    }

    // Controller Service Method [GET , POST , DELETE , PUT]


    public List<StatusDTO> getAllStatus(String boardId){
        boardServiceV3.isExist(boardId);
        List <StatusDTO> statusList = listMapper.mapList( statusRepository.findAllByBoardIdOrBoardIdIsNullOrderById(boardId), StatusDTO.class,mapper);
        List <StatusV3> editableDefaultStatus = statusRepository.findEditableDefaultStatus();
        for (StatusV3 editableStatus : editableDefaultStatus) {
            int configIndex = statusRepository.findRowIndexOfEditableDefaultStatusByStatusId(editableStatus.getId());
            char[] config = boardServiceV3.findById(boardId).getDefaultStatusConfig().toCharArray();
            if (config[configIndex - 1] == '0') {
                statusList.removeIf(statusDTO -> statusDTO.getId().equals(editableStatus.getId()));
            }
        }
        statusList.forEach(status -> status.setCount(taskRepository.countByStatusIdAndBoardId(status.getId(), boardId)));
        return statusList;

    }

    public Object getStatusById(Integer id , String boardId){
        boardServiceV3.isExist(boardId);
        StatusDTO status = mapper.map(findByIdAndBoardId(id,boardId),StatusDTO.class);
        status.setCount(taskRepository.countByStatusIdAndBoardId(id,boardId));
        return status;
    }

    @Transactional
    public StatusV3 addStatus(StatusDTO status,String boardId){
        boardServiceV3.isExist(boardId);
        status.setId(null);
        status.setBoardId(boardId);
        validateStatusDTOField(status);
        StatusV3 newStatus = mapper.map(status, StatusV3.class);
        return statusRepository.save(newStatus);
    }

    @Transactional
    public StatusV3 updateStatusById(Integer id, StatusDTO status,String boardId){
        boardServiceV3.isExist(boardId);
        StatusV3 updateStatus = findByIdAndBoardId(id,boardId);
        if (updateStatus.getIsPredefined().booleanValue()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be modified",updateStatus.getName()));
        }
        if(updateStatus.getBoardId() == null){
            disableDefaultStatus(boardId, updateStatus);
            Integer taskAmount = taskRepository.countByStatusIdAndBoardId(id,boardId);
            StatusV3 duplicateStatus = addStatus(status,boardId);
            if(taskAmount > 0){
                transferTasksStatus(updateStatus, duplicateStatus, boardId);
            }
            return duplicateStatus;
        }
        status.setId(id);
        status.setBoardId(boardId);
        validateStatusDTOField(status);
        return statusRepository.save(mapper.map(status, StatusV3.class));
    }

    @Transactional
    public StatusV3 deleteStatusById(Integer id,String boardId){
        boardServiceV3.isExist(boardId);
        StatusV3 deleteStatus = findByIdAndBoardId(id,boardId);
        Integer taskAmount = taskRepository.countByStatusIdAndBoardId(id,boardId);
        if (deleteStatus.getIsPredefined().booleanValue()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be deleted",deleteStatus.getName()));
        }
        if(taskAmount > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be deleted. There are tasks currently associated with this status.",deleteStatus.getName()));
        }
        if(deleteStatus.getBoardId() == null){
            disableDefaultStatus(boardId,deleteStatus);
            return deleteStatus;
        }
        statusRepository.delete(deleteStatus);
        return deleteStatus;
    }

    @Transactional
    public StatusV3 transferAndDeleteStatus(Integer oldId, Integer newId, String boardId){
        boardServiceV3.isExist(boardId);
        if (Objects.equals(oldId, newId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"destination status for task transfer must be different from current status");
        }
        StatusV3 oldStatus = findByIdAndBoardId(oldId,boardId);
        StatusV3 newStatus = findByIdAndBoardId(newId,boardId);
        BoardV3 currentBoard = boardServiceV3.findById(boardId);
        Integer oldStatusTaskAmount = taskRepository.countByStatusIdAndBoardId(oldId,boardId);
        Integer newStatusTaskAmount = taskRepository.countByStatusIdAndBoardId(newId,boardId);
        if(newStatusTaskAmount + oldStatusTaskAmount > currentBoard.getTaskLimitPerStatus() && currentBoard.getIsTaskLimitEnabled().booleanValue() && !newStatus.getIsPredefined().booleanValue()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("The status %s will have too many tasks",newStatus.getName()));
        }
        if (oldStatusTaskAmount == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unable to transfer task. The current status does not contain any tasks.");
        }
        if (oldStatus.getIsPredefined().booleanValue()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("%s cannot be deleted",oldStatus.getName()));
        }
        transferTasksStatus(oldStatus, newStatus, boardId);
        if(oldStatus.getBoardId() == null){
            disableDefaultStatus(boardId, oldStatus);
            return oldStatus;
        }
        statusRepository.delete(oldStatus);
        return oldStatus;
    }

}
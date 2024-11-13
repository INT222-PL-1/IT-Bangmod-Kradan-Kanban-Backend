package sit.int221.itbkkbackend.v2.services;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v2.dtos.BoardDTO;
import sit.int221.itbkkbackend.v2.dtos.StatusDTO;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.repositories.BoardRepositoryV2;
import sit.int221.itbkkbackend.v2.repositories.StatusRepositoryV2;
import sit.int221.itbkkbackend.v2.repositories.TaskRepositoryV2;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class BoardServiceV2 {
    private final BoardRepositoryV2 boardRepository;
    private final ModelMapper mapper;
    private final ListMapper listMapper;
    private final StatusRepositoryV2 statusRepository;
    private final TaskRepositoryV2 taskRepository;

    public BoardServiceV2(BoardRepositoryV2 boardRepository, ModelMapper mapper, ListMapper listMapper, StatusRepositoryV2 statusRepository, TaskRepositoryV2 taskRepository) {
        this.boardRepository = boardRepository;
        this.mapper = mapper;
        this.listMapper = listMapper;
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
    }

    public BoardV2 findById(Integer id){
        if (id == null){id = 1;}
        return boardRepository.findById(id).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"boardId does not exist")
        );
    }

    public List<BoardDTO> findAllBoard(){
        return listMapper.mapList(boardRepository.findAll(),BoardDTO.class,mapper);
    }

    @Transactional
    public BoardDTO updateBoardById(Integer id, Map<String, Optional<Object>> updateAttribute){
        BoardV2 updateBoard =  findById(id);
        List<String> validUpdateInfo = new ArrayList<>(Arrays.asList("isTaskLimitEnabled","taskLimitPerStatus")).stream().filter(updateAttribute::containsKey).toList();
        for (String attribute : validUpdateInfo) {
            Object value = updateAttribute.get(attribute).isPresent() ? updateAttribute.get(attribute).get() : null;
            if(value == null) {continue;}
            try {
                Field updateInfo = BoardV2.class.getDeclaredField(attribute);
                updateInfo.setAccessible(true);
                updateInfo.set(updateBoard, value);
                updateInfo.setAccessible(false);
            } catch (Exception exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

        }
        BoardDTO board = mapper.map(updateBoard, BoardDTO.class);
        List<StatusDTO> exceedLimitStatus = listMapper.mapList(statusRepository.findStatusWithTasksExceedingLimit(id, updateBoard.getTaskLimitPerStatus()), StatusDTO.class,mapper);
        exceedLimitStatus.forEach(status -> {
            status.setBoardId(id);
            status.setCount(taskRepository.countByStatusId(status.getId()));
        });
        board.setExceedLimitStatus(exceedLimitStatus);
        return board;
    }
}

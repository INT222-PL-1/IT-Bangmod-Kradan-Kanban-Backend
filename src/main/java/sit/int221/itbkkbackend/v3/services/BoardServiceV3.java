package sit.int221.itbkkbackend.v3.services;

import io.viascom.nanoid.NanoId;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.Users;
import sit.int221.itbkkbackend.auth.UsersDTO;
import sit.int221.itbkkbackend.auth.UsersRepository;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.BoardDTO;
import sit.int221.itbkkbackend.v3.dtos.StatusDTO;
import sit.int221.itbkkbackend.v3.entities.BoardV3;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.StatusRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.TaskRepositoryV3;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Service
public class BoardServiceV3 {

    @Autowired
    private BoardRepositoryV3 boardRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private StatusRepositoryV3 statusRepository;
    @Autowired
    private TaskRepositoryV3 taskRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ValidatingServiceV3 validatingService;
    public BoardV3 findById(String id){
        return boardRepository.findById(id).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"boardId does not exist")
        );
    }

    public void isExist(String id){
        if(!boardRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format("Board id %s does not exist !!!",id));
        }
    }

    // Controller Service Method [GET , PATCH , POST]

    public List<BoardDTO> findAllBoards(){
        Users user = usersRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return listMapper.mapList(user == null ? boardRepository.findAllByVisibilityIsPublic() : boardRepository.findAllByOwnerOid(user.getOid()) ,BoardDTO.class,mapper);
    }

    @Transactional
    public BoardDTO updateBoardById(String id, Map<String, Optional<Object>> updateAttribute){
        BoardV3 updateBoard =  findById(id);
        List<String> validUpdateInfo = new ArrayList<>(Arrays.asList("isTaskLimitEnabled","taskLimitPerStatus","defaultStatusConfig","visibility")).stream().filter(updateAttribute::containsKey).toList();
        for (String attribute : validUpdateInfo) {
            Object value = updateAttribute.get(attribute).isPresent() ? updateAttribute.get(attribute).get() : null;
            if(value == null) {continue;}
            try {
                Field updateInfo = BoardV3.class.getDeclaredField(attribute);
                updateInfo.setAccessible(true);
                updateInfo.set(updateBoard, value);
                updateInfo.setAccessible(false);
            } catch (Exception exception) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

        }
        BoardDTO board = mapper.map(updateBoard, BoardDTO.class);
        validatingService.validateBoardDTO(board);
        log.info(board.getVisibility());
        List<StatusDTO> exceedLimitStatus = listMapper.mapList(statusRepository.findStatusWithTasksExceedingLimit(id, updateBoard.getTaskLimitPerStatus()), StatusDTO.class,mapper);
        exceedLimitStatus.forEach(status -> {
            status.setBoardId(id);
            status.setCount(taskRepository.countByStatusIdAndBoardId(status.getId(),id));
        });
        board.setExceedLimitStatus(exceedLimitStatus);
        return board;
    }

    @Transactional
    public BoardDTO addBoard(BoardDTO board){
        validatingService.validateBoardDTO(board);

        // get oid from token , initialize board and save
        Users user = usersRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        BoardV3 newBoard = new BoardV3();
        String newBoardId = NanoId.generate(10);
        while(boardRepository.existsById(newBoardId)){
            newBoardId = NanoId.generate(10);
        }
        newBoard.setId(newBoardId);
        newBoard.setName(board.getName());
        newBoard.setOwnerOid(user.getOid());
        BoardV3 createdBoard = boardRepository.saveAndFlush(newBoard);
        entityManager.refresh(createdBoard);

        // craft response and return
        BoardDTO createdBoardDTO = mapper.map(createdBoard,BoardDTO.class);
        UsersDTO owner = new UsersDTO(user.getOid(), user.getUsername());
        createdBoardDTO.setOwner(owner);
        return createdBoardDTO;
    }
}

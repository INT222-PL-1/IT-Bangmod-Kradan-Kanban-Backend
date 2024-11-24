package sit.int221.itbkkbackend.v3.services;

import io.viascom.nanoid.NanoId;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.CustomUserDetails;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.dtos.UsersDTO;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.BoardDTO;
import sit.int221.itbkkbackend.v3.dtos.BoardListDTO;
import sit.int221.itbkkbackend.v3.dtos.StatusDTO;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3;
import sit.int221.itbkkbackend.v3.entities.BoardV3;
import sit.int221.itbkkbackend.v3.entities.UserV3;
import sit.int221.itbkkbackend.v3.repositories.*;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Service
public class BoardServiceV3 {
    private final BoardRepositoryV3 boardRepository;
    private final ModelMapper mapper;
    private final ListMapper listMapper;
    private final StatusRepositoryV3 statusRepository;
    private final TaskRepositoryV3 taskRepository;
    private final UsersRepository userSharedRepository;
    private final EntityManager entityManager;
    private final ValidatingServiceV3 validatingService;
    private final BoardPermissionRepositoryV3 boardPermissionRepository;
    private final UserRepositoryV3 userRepository;

    public BoardServiceV3(BoardRepositoryV3 boardRepository, ModelMapper mapper, ListMapper listMapper, StatusRepositoryV3 statusRepository, TaskRepositoryV3 taskRepository, UsersRepository userSharedRepository, EntityManager entityManager, ValidatingServiceV3 validatingService, BoardPermissionRepositoryV3 boardPermissionRepository, UserRepositoryV3 userRepository) {
        this.boardRepository = boardRepository;
        this.mapper = mapper;
        this.listMapper = listMapper;
        this.statusRepository = statusRepository;
        this.taskRepository = taskRepository;
        this.userSharedRepository = userSharedRepository;
        this.entityManager = entityManager;
        this.validatingService = validatingService;
        this.boardPermissionRepository = boardPermissionRepository;
        this.userRepository = userRepository;
    }

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

    public BoardListDTO findAllBoards(CustomUserDetails userDetails){
        UserV3 user = userRepository.findByOid(userDetails.getOid());
        if (user != null){
            List<BoardDTO> personalBoards = boardRepository.findAllPersonalBoards(user.getOid());
            List<BoardDTO> collaborativeBoards = boardRepository.findAllCollaborativeBoards(user.getOid());
            return new BoardListDTO(personalBoards,collaborativeBoards);
        } else {
            return new BoardListDTO();
        }
    }

    public BoardDTO findByIdAndOwnerId(String id){
        BoardV3 board = findById(id);
        BoardDTO foundedBoard = mapper.map(board,BoardDTO.class);
        UserV3 user = userRepository.findOwnerOfBoardId(id);
        UsersDTO owner = new UsersDTO(user.getOid(),user.getUsername(),user.getName());
        foundedBoard.setOwner(owner);
        return foundedBoard;
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
        List<StatusDTO> exceedLimitStatus = listMapper.mapList(statusRepository.findStatusWithTasksExceedingLimit(id, updateBoard.getTaskLimitPerStatus()), StatusDTO.class,mapper);
        exceedLimitStatus.forEach(status -> {
            status.setBoardId(id);
            status.setCount(taskRepository.countByStatusIdAndBoardId(status.getId(),id));
        });
        board.setExceedLimitStatus(exceedLimitStatus);
        return board;
    }

    @Transactional
    public BoardDTO addBoard(BoardDTO board,CustomUserDetails userDetails){
        validatingService.validateBoardDTO(board);

        // get oid from token , initialize board and save
        UserV3 user = userRepository.findByOid(userDetails.getOid());
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

        //add ownership
        BoardPermissionV3 boardPermission = new BoardPermissionV3();
        BoardPermissionV3.BoardUserKey boardUserKey = new BoardPermissionV3.BoardUserKey();
        boardUserKey.setBoardId(newBoardId);
        boardUserKey.setOid(user.getOid());
        boardPermission.setBoardUserKey(boardUserKey);
        boardPermission.setAccessRight("OWNER");
        boardPermission.setInviteStatus(null);
        boardPermissionRepository.save(boardPermission);

        // craft response and return
        BoardDTO createdBoardDTO = mapper.map(createdBoard,BoardDTO.class);
        UsersDTO owner = new UsersDTO(user.getOid(), user.getUsername());
        createdBoardDTO.setOwner(owner);
        return createdBoardDTO;
    }
}

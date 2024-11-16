package sit.int221.itbkkbackend.v3.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.CustomUserDetails;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.exceptions.CollaboratorNotFoundException;
import sit.int221.itbkkbackend.exceptions.UserEmailNotFoundException;
import sit.int221.itbkkbackend.v3.dtos.AddCollaboratorDTO;
import sit.int221.itbkkbackend.v3.dtos.CollaboratorDTO;
import sit.int221.itbkkbackend.v3.dtos.CollaboratorDetailsDTO;
import sit.int221.itbkkbackend.v3.dtos.UpdateCollaboratorDTO;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3;
import sit.int221.itbkkbackend.v3.entities.UserV3;
import sit.int221.itbkkbackend.v3.repositories.BoardPermissionRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.UserRepositoryV3;

import java.util.List;

@Slf4j
@Service
public class BoardPermissionServiceV3 {
    private final BoardPermissionRepositoryV3 boardPermissionRepository;
    private final BoardRepositoryV3 boardRepository;
    private final UserRepositoryV3 userRepository;
    private final UsersRepository userSharedRepository;
    private final ModelMapper mapper;
    private final ValidatingServiceV3 validatingService;
    private final EmailService emailService;

    public BoardPermissionServiceV3(BoardPermissionRepositoryV3 boardPermissionRepository, BoardRepositoryV3 boardRepository, UserRepositoryV3 userRepository, UsersRepository userSharedRepository, ModelMapper mapper, ValidatingServiceV3 validatingService, EmailService emailService) {
        this.boardPermissionRepository = boardPermissionRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.userSharedRepository = userSharedRepository;
        this.mapper = mapper;
        this.validatingService = validatingService;
        this.emailService = emailService;
    }

    public List<CollaboratorDTO> findAllCollaborator(String boardId){
        return boardPermissionRepository.findAllCollaboratorByBoardId(boardId);
    }

    public CollaboratorDetailsDTO findCollaboratorByOid(String boardId,String oid){
        CollaboratorDetailsDTO collaborator = boardPermissionRepository.findCollaboratorByBoardIdAndOid(boardId,oid);
        if(collaborator == null){
            throw new CollaboratorNotFoundException(HttpStatus.NOT_FOUND,oid);
        }
        collaborator.setOwnerName(userRepository.findOwnerOfBoardId(boardId).getName());
        collaborator.setBoardName(boardRepository.getBoardNameFromId(boardId));
        return collaborator;
    }

    public CollaboratorDTO addPermissionOnBoard(String boardId, AddCollaboratorDTO collaborator, String requestUrl) {
        validatingService.validateAddCollaboratorDTO(collaborator);
        BoardPermissionV3 boardPermission = new BoardPermissionV3();
        BoardPermissionV3.BoardUserKey boardUserKey = new BoardPermissionV3.BoardUserKey();
        Users user = userSharedRepository.findByEmail(collaborator.getEmail());

        // check there's exist user with given email.
        if(user == null){
            throw new UserEmailNotFoundException(HttpStatus.NOT_FOUND,collaborator.getEmail());
        }
        // check there's exist user with given email has logged in or be board owner.
        if (!userRepository.existsById(user.getOid())){
            userRepository.save(mapper.map(user,UserV3.class));
        } else if (boardPermissionRepository.isBoardOwner(boardId, user.getOid()) || boardPermissionRepository.existsCollaboratorByBoardIdAndOid(boardId,user.getOid())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Provided user with email %s can't be collaborator.",collaborator.getEmail()));
        }
        
        boardUserKey.setBoardId(boardId);
        boardUserKey.setOid(user.getOid());
        boardPermission.setBoardUserKey(boardUserKey);
        boardPermission.setAccessRight(collaborator.getAccessRight());
        boardPermission.setInviteStatus("PENDING");
        boardPermissionRepository.save(boardPermission);

        try {
            CustomUserDetails senderDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String boardName = boardRepository.getBoardNameFromId(boardId);

            emailService.sendInvitationEmail(user, collaborator.getAccessRight(), senderDetails, boardId, boardName, requestUrl); 
        } catch (Exception e) {
            log.error("Error sending email to " + collaborator.getEmail(), e);
        }
        return new CollaboratorDTO(user.getOid(), user.getName(), collaborator.getEmail(), collaborator.getAccessRight());
    }

    public UpdateCollaboratorDTO updateAccessRight(String boardId, String oid, UpdateCollaboratorDTO collaborator){
        validatingService.validateUpdateCollaboratorDTO(collaborator);
        BoardPermissionV3 boardPermission = boardPermissionRepository.findBoardPermissionV3(boardId,oid);
        if(boardPermission == null){
            throw new CollaboratorNotFoundException(HttpStatus.NOT_FOUND,oid);
        }
        boardPermission.setAccessRight(collaborator.getAccessRight());
        boardPermissionRepository.save(boardPermission);
        return collaborator;
    }
    public void removeAccessRight(String boardId, String oid){
        BoardPermissionV3 boardPermission = boardPermissionRepository.findBoardPermissionV3(boardId,oid);
        if(boardPermission == null){
            throw new CollaboratorNotFoundException(HttpStatus.NOT_FOUND,oid);
        }
        boardPermissionRepository.delete(boardPermission);
    }

    public void updateInviteStatus(String boardId, String oid){
        BoardPermissionV3 boardPermission = boardPermissionRepository.findBoardPermissionV3(boardId,oid);
        if(boardPermission == null){
            throw new CollaboratorNotFoundException(HttpStatus.NOT_FOUND,oid);
        }
        boardPermission.setInviteStatus("JOINED");
        boardPermissionRepository.save(boardPermission);
    }

}

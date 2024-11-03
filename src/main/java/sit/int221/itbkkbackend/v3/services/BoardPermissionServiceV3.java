package sit.int221.itbkkbackend.v3.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.exceptions.CollaboratorNotFoundException;
import sit.int221.itbkkbackend.exceptions.UserEmailNotFoundException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.AddCollaboratorDTO;
import sit.int221.itbkkbackend.v3.dtos.CollaboratorDTO;
import sit.int221.itbkkbackend.v3.dtos.UpdateCollaboratorDTO;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3;
import sit.int221.itbkkbackend.v3.entities.UserV3;
import sit.int221.itbkkbackend.v3.repositories.BoardPermissionRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.UserRepositoryV3;

import java.util.List;

@Slf4j
@Service
public class BoardPermissionServiceV3 {
    @Autowired
    BoardPermissionRepositoryV3 boardPermissionRepository;
    @Autowired
    UserRepositoryV3 userRepository;
    @Autowired
    UsersRepository userSharedRepository;
    @Autowired
    ModelMapper mapper;
    @Autowired
    ListMapper listMapper;
    @Autowired
    ValidatingServiceV3 validatingService;

    public List<CollaboratorDTO> findAllCollaborator(String boardId){
        List<CollaboratorDTO> collaborators = boardPermissionRepository.findAllCollaboratorByBoardId(boardId);
        return collaborators;
    }

    public CollaboratorDTO findCollaboratorByOid(String boardId,String oid){
        CollaboratorDTO collaborator = boardPermissionRepository.findCollaboratorByBoardIdAndOid(boardId,oid);
        if(collaborator == null){
            throw new CollaboratorNotFoundException(HttpStatus.NOT_FOUND,oid);
        }
        return collaborator;
    }

    public CollaboratorDTO addPermissionOnBoard(String boardId, AddCollaboratorDTO collaborator){
        validatingService.validateAddCollaboratorDTO(collaborator);
        BoardPermissionV3 boardPermission = new BoardPermissionV3();
        BoardPermissionV3.BoardUserKey boardUserKey = new BoardPermissionV3.BoardUserKey();
        Users user = userSharedRepository.findByEmail(collaborator.getEmail());

        // check there's exist user with given email.
        if(user == null){
            throw new UserEmailNotFoundException(HttpStatus.NOT_FOUND,collaborator.getEmail());
        }
        // check there's exist user with given email has logged in or be board owner.
        if (userRepository.existsById(user.getOid()) == false){
            userRepository.save(mapper.map(user,UserV3.class));
        } else if (boardPermissionRepository.isBoardOwner(boardId,user.getOid()) || boardPermissionRepository.existsCollaboratorByBoardIdAndOid(boardId,user.getOid())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,String.format("Provided user with email %s can't be collaborator.",collaborator.getEmail()));
        }
        boardUserKey.setBoardId(boardId);
        boardUserKey.setOid(user.getOid());
        boardPermission.setBoardUserKey(boardUserKey);
        boardPermission.setAccessRight(collaborator.getAccessRight());
        boardPermissionRepository.save(boardPermission);
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

}

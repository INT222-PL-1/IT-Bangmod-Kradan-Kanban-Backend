package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v3.dtos.CollaboratorDTO;
import sit.int221.itbkkbackend.v3.dtos.CollaboratorDetailsDTO;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3.BoardUserKey;

import java.util.List;

public interface BoardPermissionRepositoryV3 extends JpaRepository<BoardPermissionV3,BoardUserKey> {
    @Query("select new sit.int221.itbkkbackend.v3.dtos.CollaboratorDTO(u.oid, u.name, u.email, bp.accessRight, bp.inviteStatus, bp.addedOn) from BoardPermissionV3 bp join UserV3 u on u.oid = bp.boardUserKey.oid where bp.boardUserKey.boardId = :boardId and bp.accessRight != 'OWNER'")
    List<CollaboratorDTO> findAllCollaboratorByBoardId(String boardId);

    @Query("select new sit.int221.itbkkbackend.v3.dtos.CollaboratorDetailsDTO(u.oid, u.name, u.email, bp.accessRight ,bp.inviteStatus, bp.addedOn) from BoardPermissionV3 bp join UserV3 u on u.oid = bp.boardUserKey.oid where bp.boardUserKey.boardId = :boardId and u.oid = :oid and bp.accessRight != 'OWNER'")
    CollaboratorDetailsDTO findCollaboratorByBoardIdAndOid(String boardId, String oid);

    @Query("select case when count(bp) > 0 then true else false end from BoardPermissionV3 bp join UserV3 u on u.oid = bp.boardUserKey.oid where bp.boardUserKey.boardId = :boardId and u.oid = :oid and bp.accessRight != 'OWNER'")
    Boolean existsCollaboratorByBoardIdAndOid(String boardId,String oid);

    @Query("select bp from BoardPermissionV3 bp join UserV3 u on u.oid = bp.boardUserKey.oid where bp.boardUserKey.boardId = :boardId and u.oid = :oid")
    BoardPermissionV3 findBoardPermissionV3(String boardId,String oid);

    @Query("SELECT CASE WHEN COUNT(bp) > 0 THEN true ELSE false END " +
            "FROM BoardPermissionV3 bp " +
            "WHERE bp.boardUserKey.boardId = :boardId " +
            "AND bp.boardUserKey.oid = :oid " +
            "AND bp.accessRight = 'OWNER'")
    Boolean isBoardOwner(@Param("boardId") String boardId, @Param("oid") String oid);

    @Query("select bp.accessRight from BoardPermissionV3 bp where bp.boardUserKey.boardId = :boardId and bp.boardUserKey.oid = :oid and ( bp.inviteStatus is null or bp.inviteStatus = 'JOINED' )")
    String getAccessRightByBoardIdAndOid(@Param("boardId") String boardId, @Param("oid") String oid);

}

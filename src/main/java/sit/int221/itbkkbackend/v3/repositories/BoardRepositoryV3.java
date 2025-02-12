package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v3.dtos.BoardDTO;
import sit.int221.itbkkbackend.v3.entities.BoardV3;

import java.util.List;

public interface BoardRepositoryV3 extends JpaRepository<BoardV3, String> {

    Boolean existsBoardV3sByIdAndVisibility(String boardId,String visibility);

    @Query("select new sit.int221.itbkkbackend.v3.dtos.BoardDTO(b.id,b.name, b.isTaskLimitEnabled, b.taskLimitPerStatus, b.visibility , bp.accessRight ,bp2.boardUserKey.oid ,bp2.user.name)" +
            " from BoardV3 b join BoardPermissionV3  bp on bp.boardUserKey.boardId = b.id" +
            " join BoardPermissionV3 bp2 on bp2.boardUserKey.boardId = bp.boardUserKey.boardId and bp2.accessRight = 'OWNER' " +
            " where bp.boardUserKey.oid = :oid" )
    List<BoardDTO> findAllByOwnerOidWithCollabs(@Param("oid") String oid);

    @Query("select new sit.int221.itbkkbackend.v3.dtos.BoardDTO(b.id,b.name, b.isTaskLimitEnabled, b.taskLimitPerStatus, b.visibility , bp.accessRight ,bp.boardUserKey.oid ,bp.user.name)" +
            " from BoardV3 b join BoardPermissionV3  bp on bp.boardUserKey.boardId = b.id" +
            " where bp.accessRight = 'OWNER' and bp.boardUserKey.oid = :oid" )
    List<BoardDTO> findAllPersonalBoards(@Param("oid") String oid);

    @Query("select new sit.int221.itbkkbackend.v3.dtos.BoardDTO(b.id,b.name, b.isTaskLimitEnabled, b.taskLimitPerStatus, b.visibility , bp.accessRight ,bp.inviteStatus, bp2.boardUserKey.oid ,bp2.user.name)" +
            " from BoardV3 b join BoardPermissionV3  bp on bp.boardUserKey.boardId = b.id" +
            " join BoardPermissionV3 bp2 on bp2.boardUserKey.boardId = bp.boardUserKey.boardId and bp2.accessRight = 'OWNER' " +
            " where bp.boardUserKey.oid = :oid and bp.accessRight != 'OWNER'" )
    List<BoardDTO> findAllCollaborativeBoards(@Param("oid") String oid);

    @Query("select b.name from BoardV3 b where b.id = :boardId ")
    String getBoardNameFromId(@Param("boardId") String boardId);
    @Query("select b from BoardV3 b where b.visibility = 'PUBLIC'")
    List<BoardV3> findAllByVisibilityIsPublic();
}

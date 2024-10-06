package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v3.entities.BoardV3;

import java.util.List;

public interface BoardRepositoryV3 extends JpaRepository<BoardV3, String> {
    List<BoardV3> findAllByOwnerOid(String ownerOid);

    @Query("select b from BoardV3 b join BoardPermissionV3  bp on bp.boardUserKey.boardId = b.id where bp.boardUserKey.oid = :oid")
    List<BoardV3> findAllByOwnerOidWithCollabs(@Param("oid") String oid);

    Boolean existsBoardV3sByIdAndVisibility(String boardId,String visibility);

    Boolean existsBoardV3sByIdAndOwnerOid(String boardId,String ownerId);
    @Query("select b from BoardV3 b where b.visibility = 'PUBLIC'")
    List<BoardV3> findAllByVisibilityIsPublic();
}

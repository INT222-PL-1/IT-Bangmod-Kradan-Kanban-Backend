package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v3.entities.UserV3;

public interface UserRepositoryV3 extends JpaRepository<UserV3,String> {

    @Query("select u from UserV3 u join BoardPermissionV3 bp on bp.boardUserKey.oid = u.oid where bp.accessRight = 'OWNER' and bp.boardUserKey.boardId = :boardId ")
    UserV3 findOwnerOfBoardId(@Param("boardId") String boardId);
}

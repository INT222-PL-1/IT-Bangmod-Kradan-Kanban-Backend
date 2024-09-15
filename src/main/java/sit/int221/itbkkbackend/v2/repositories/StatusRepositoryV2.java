package sit.int221.itbkkbackend.v2.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v2.entities.StatusV2;

import java.util.List;

public interface StatusRepositoryV2 extends JpaRepository<StatusV2,Integer> {
    StatusV2 findByName(String name);

    @Query("SELECT DISTINCT s FROM StatusV3 s JOIN s.tasks t JOIN t.board b WHERE b.id = :boardId AND s.is_fixed_status = false GROUP BY s HAVING COUNT(t) > :taskLimit")
    List<StatusV2> findStatusWithTasksExceedingLimit(@Param("boardId") Integer boardId, @Param("taskLimit") Integer taskLimit);
}

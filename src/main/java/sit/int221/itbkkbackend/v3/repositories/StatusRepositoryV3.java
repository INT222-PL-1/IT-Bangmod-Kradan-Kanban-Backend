package sit.int221.itbkkbackend.v3.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v3.entities.StatusV3;

import java.util.List;

public interface StatusRepositoryV3 extends JpaRepository<StatusV3,Integer> {
    StatusV3 findByName(String name);

    List<StatusV3> findAllByBoardIdOrBoardIdIsNullOrderById(String boardId);

    @Query("select s from StatusV3 s where (s.boardId is null or s.boardId = :boardId) and s.name = :name")
    List<StatusV3> findByNameAndBoardIdIsNotNull(@Param("name") String name, @Param("boardId") String boardId);

    @Query("SELECT DISTINCT s FROM StatusV3 s JOIN s.tasks t JOIN t.board b WHERE b.id = :boardId AND s.isPredefined = false GROUP BY s HAVING COUNT(t) > :taskLimit")
    List<StatusV3> findStatusWithTasksExceedingLimit(@Param("boardId") String boardId, @Param("taskLimit") Integer taskLimit);

    @Query("select s from StatusV3 s where s.boardId is NULL and s.isPredefined = false")
    List<StatusV3> findEditableDefaultStatus();

    @Query(value = "SELECT rowIndex FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY `status_id`) AS rowIndex FROM `status_v3` WHERE `board_id` IS NULL AND is_predefined = FALSE) AS temp WHERE temp.`status_id` = :statusId", nativeQuery = true)
    Integer findRowIndexOfEditableDefaultStatusByStatusId(@Param("statusId") Integer statusId);
}
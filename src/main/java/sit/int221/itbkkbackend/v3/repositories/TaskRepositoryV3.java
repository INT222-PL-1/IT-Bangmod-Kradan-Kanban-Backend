package sit.int221.itbkkbackend.v3.repositories;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.itbkkbackend.v3.dtos.SimpleTaskDTO;
import sit.int221.itbkkbackend.v3.entities.StatusV3;
import sit.int221.itbkkbackend.v3.entities.TaskV3;

import java.util.List;

public interface TaskRepositoryV3 extends JpaRepository<TaskV3, Integer> {
    List<TaskV3> findAllByBoardId(String boardId, Sort sort);

    Integer countByStatusIdAndBoardId(Integer statusId, String boardId);

    TaskV3 findByIdAndBoardId(Integer id, String boardId);

    boolean existsByIdAndBoardId(Integer taskId, String boardId);

    @Query("SELECT new sit.int221.itbkkbackend.v3.dtos.SimpleTaskDTO(" +
            "t.id, t.title, t.assignees, t.status, t.boardId, " +
            " t.updatedOn, COUNT(f)) " +
            "FROM TaskV3 t LEFT JOIN t.files f " +
            "GROUP BY t.id, t.title, t.description, t.assignees, t.statusId, t.boardId, t.updatedOn")
    List<SimpleTaskDTO> findAllTasksWithFileCounts(@Param("boardId") String boardId, Sort sort);
    @Modifying
    @Query("UPDATE TaskV3 t SET t.status = :newStatus WHERE t.status = :oldStatus AND t.boardId = :newStatusBoardId")
    void updateAllStatusByStatusAndBoardId(StatusV3 oldStatus, StatusV3 newStatus, String newStatusBoardId);

}

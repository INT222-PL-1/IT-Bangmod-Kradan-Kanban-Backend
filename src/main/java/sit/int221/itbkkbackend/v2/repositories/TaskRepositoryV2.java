package sit.int221.itbkkbackend.v2.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.itbkkbackend.v2.entities.StatusV2;
import sit.int221.itbkkbackend.v2.entities.TaskV2;

import java.util.List;

public interface TaskRepositoryV2 extends JpaRepository<TaskV2,Integer> {
    List<TaskV2> findAllByStatus(StatusV2 status);

    @Modifying
    @Query("UPDATE TaskV2 t SET t.status = :newStatus WHERE t.status = :oldStatus")
    void updateAllStatusByStatus(StatusV2 oldStatus, StatusV2 newStatus);
}

package sit.int221.itbkkbackend.v2.repositories;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sit.int221.itbkkbackend.v2.entities.Status;
import sit.int221.itbkkbackend.v2.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    List<Task> findAllByStatus(Status status);

    @Modifying
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus")
    void updateAllStatusByStatus(Status oldStatus, Status newStatus);
}

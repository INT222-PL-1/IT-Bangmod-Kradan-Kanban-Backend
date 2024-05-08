package sit.int221.itbkkbackend.v1.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v1.entities.TaskV1;

public interface TaskRepositoryV1 extends JpaRepository<TaskV1,Integer> {
}

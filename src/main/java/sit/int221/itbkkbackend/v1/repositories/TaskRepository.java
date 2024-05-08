package sit.int221.itbkkbackend.v1.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v1.entities.Task;

public interface TaskRepository extends JpaRepository<Task,Integer> {
}

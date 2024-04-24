package sit.int221.itbkkbackend.repositories;


import sit.int221.itbkkbackend.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Integer> {
}

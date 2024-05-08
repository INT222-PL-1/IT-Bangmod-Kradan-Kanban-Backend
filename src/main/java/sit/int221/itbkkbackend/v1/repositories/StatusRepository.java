package sit.int221.itbkkbackend.v1.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v1.entities.Status;

public interface StatusRepository extends JpaRepository<Status,Integer> {
    Status findByName(String name);
}

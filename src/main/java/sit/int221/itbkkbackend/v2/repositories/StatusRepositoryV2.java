package sit.int221.itbkkbackend.v2.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v2.entities.StatusV2;

public interface StatusRepositoryV2 extends JpaRepository<StatusV2,Integer> {
    StatusV2 findByName(String name);
}

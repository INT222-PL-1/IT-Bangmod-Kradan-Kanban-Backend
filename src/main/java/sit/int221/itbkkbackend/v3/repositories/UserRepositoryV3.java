package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v3.entities.UserV3;

public interface UserRepositoryV3 extends JpaRepository<UserV3,String> {
}

package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.itbkkbackend.v3.entities.UserV3;

import java.util.Optional;

public interface UserRepositoryV3 extends JpaRepository<UserV3,String> {
    UserV3 findByEmail(String email);
}

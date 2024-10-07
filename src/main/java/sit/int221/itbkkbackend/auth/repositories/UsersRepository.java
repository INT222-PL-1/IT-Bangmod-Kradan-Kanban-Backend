package sit.int221.itbkkbackend.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.auth.entities.Users;

public interface UsersRepository extends JpaRepository<Users,String> {
    Users findByUsername(String username);

    Users findByOid(String oid);

    Users findByEmail(String email);


}

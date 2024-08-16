package sit.int221.itbkkbackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,String> {
    Users findByUsername(String username);
}

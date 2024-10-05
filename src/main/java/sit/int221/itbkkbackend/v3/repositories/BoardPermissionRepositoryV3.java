package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3;
import sit.int221.itbkkbackend.v3.entities.BoardPermissionV3.BoardUserKey;

public interface BoardPermissionRepositoryV3 extends JpaRepository<BoardPermissionV3,BoardUserKey> {
}

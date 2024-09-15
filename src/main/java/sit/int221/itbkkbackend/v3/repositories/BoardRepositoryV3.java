package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.itbkkbackend.v3.entities.BoardV3;

import java.util.List;

public interface BoardRepositoryV3 extends JpaRepository<BoardV3, String> {
    List<BoardV3> findAllByOwnerOid(String ownerOid);
}

package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.itbkkbackend.v3.entities.FileV3;

import java.io.File;
import java.util.List;

public interface FileRepositoryV3 extends JpaRepository<FileV3, FileV3.FileKey> {
    @Query("select f from FileV3 f where f.fileKey.taskId = :taskId")
    List<FileV3> findAllByTaskId(@Param("taskId") Integer taskId);

    @Transactional
    @Modifying
    @Query("DELETE FROM FileV3 f WHERE f.fileKey.taskId = :taskId AND f.fileKey.name NOT IN :excludeNames")
    void deleteFilesByTaskIdExcludingNames(Integer taskId, List<String> excludeNames);
}

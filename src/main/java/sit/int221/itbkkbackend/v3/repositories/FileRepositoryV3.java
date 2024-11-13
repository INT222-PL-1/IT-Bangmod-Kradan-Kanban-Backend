package sit.int221.itbkkbackend.v3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.itbkkbackend.v3.entities.FileV3;

import java.util.List;

public interface FileRepositoryV3 extends JpaRepository<FileV3, FileV3.FileKey> {

    @Query("SELECT COUNT(f) FROM FileV3 f WHERE f.fileKey.taskId = :taskId")
    long countFilesByTaskId(@Param("taskId") Integer taskId);
    @Query("select f from FileV3 f where f.fileKey.taskId = :taskId")
    List<FileV3> findAllByTaskId(@Param("taskId") Integer taskId);

    @Query("select f from FileV3 f where f.fileKey.taskId = :taskId and f.fileKey.name = :fileName")
    FileV3 findByFileNameAndTaskId(@Param("taskId") Integer taskId, @Param("fileName") String fileName);

    @Transactional
    @Modifying
    @Query("delete from FileV3 f where f.fileKey.taskId = :taskId")
    void deleteByTaskId(@Param("taskId") Integer taskId);

    @Transactional
    @Modifying
    @Query("DELETE FROM FileV3 f WHERE f.fileKey.taskId = :taskId AND f.fileKey.name NOT IN :excludeNames")
    void deleteFilesByTaskIdExcludingNames(Integer taskId, List<String> excludeNames);
}

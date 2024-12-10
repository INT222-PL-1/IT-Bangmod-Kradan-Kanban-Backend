package sit.int221.itbkkbackend.v3.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;
import sit.int221.itbkkbackend.v3.entities.TaskV3;

import java.nio.file.Path;
import java.util.List;

public interface StorageService {

    FileV3  loadAsData(String filename, Integer taskId);

    void deleteAll(Integer taskId);

    List<FileInfoDTO> store(MultipartFile[] files, Integer taskId ,String boardId);

    List<FileInfoDTO> loadAll(TaskV3 task, String boardId, HttpServletRequest request);
    List<FileInfoDTO> loadAll(Integer taskId, String boardId, HttpServletRequest request);

    Path load(String filename, Integer taskId);

    Resource loadAsResource(String filename,Integer taskId);

}

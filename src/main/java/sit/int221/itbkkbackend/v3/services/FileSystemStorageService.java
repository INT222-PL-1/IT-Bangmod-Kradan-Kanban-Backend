package sit.int221.itbkkbackend.v3.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;
import sit.int221.itbkkbackend.v3.entities.TaskV3;
import sit.int221.itbkkbackend.v3.properties.StorageProperties;
import sit.int221.itbkkbackend.v3.repositories.FileRepositoryV3;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final FileRepositoryV3 fileRepository;

    public FileSystemStorageService(StorageProperties properties, FileRepositoryV3 fileRepository) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload location cannot be empty.");
        }
        this.fileRepository = fileRepository;
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public List<FileInfoDTO> store(MultipartFile[] files, Integer taskId, String boardId) {
        try {
            if(fileRepository.countFilesByTaskId(taskId) + files.length > 10){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The number of files for this task exceeds the limit of 10.");
            }
            Path taskDirectory = rootLocation.resolve(taskId.toString());
            if (!Files.exists(taskDirectory)) {
                Files.createDirectories(taskDirectory);
            }
            List<FileInfoDTO> fileList = new LinkedList<>();
            for (MultipartFile file : files) {
                Path destinationFile = taskDirectory.resolve(
                                Paths.get(file.getOriginalFilename()))
                        .normalize().toAbsolutePath();
                Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
                fileList.add(new FileInfoDTO(fileRepository.save(new FileV3(file,taskId)),taskId,boardId));
            }

            return fileList;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store files.", e);
        }
    }

    @Override
    public List<FileInfoDTO> loadAll(TaskV3 task, String boardId) {
        return ListMapper.mapFileListToFileInfoDTOList(task.getFiles(), task.getId(), boardId);
    }

    @Override
    public List<FileInfoDTO> loadAll(Integer taskId, String boardId) {
        return ListMapper.mapFileListToFileInfoDTOList(fileRepository.findAllByTaskId(taskId), taskId, boardId);
    }

    @Override
    public Path load(String filename, Integer taskId) {
        // Resolve the path to the file under the specified task directory
        return rootLocation.resolve(taskId.toString()).resolve(filename).normalize().toAbsolutePath();
    }

    @Override
    public Resource loadAsResource(String filename, Integer taskId) {
        try {
            Path file = load(filename, taskId);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file: " + filename, e);
        }
    }

    @Override
    public FileV3 loadAsData(String filename, Integer taskId) {
        return fileRepository.findByFileNameAndTaskId(taskId,filename);
    }

    @Override
    public void deleteAll(Integer taskId) {
        fileRepository.deleteByTaskId(taskId);
        Path taskDirectory = rootLocation.resolve(taskId.toString());
        File directory = taskDirectory.toFile();

        if (directory.exists()) {
            try {
                FileUtils.deleteDirectory(directory); // Force delete directory and all contents
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete directory: " + taskId, e);
            }
        }
    }


    public void deleteFilesExcept(Integer taskId, List<String> excludeNames) {
        try {
            // Delete files from the filesystem
            Path taskDirectory = rootLocation.resolve(taskId.toString());
            if (Files.exists(taskDirectory) && Files.isDirectory(taskDirectory)) {
                Files.list(taskDirectory)
                        .filter(path -> !excludeNames.contains(path.getFileName().toString()))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to delete file: " + path.getFileName(), e);
                            }
                        });
            }

            // Delete records from the database
            fileRepository.deleteFilesByTaskIdExcludingNames(taskId, excludeNames);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete files for task: " + taskId, e);
        }
    }

}

package sit.int221.itbkkbackend.v3.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.utils.ListMapper;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;
import sit.int221.itbkkbackend.v3.properties.StorageProperties;
import sit.int221.itbkkbackend.v3.repositories.FileRepositoryV3;

@Slf4j
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    private FileRepositoryV3 fileRepository;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload location cannot be empty.");
        }
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public List<FileInfoDTO> store(MultipartFile[] files, Integer taskId) {
        try {
            Path taskDirectory = rootLocation.resolve(taskId.toString());
            if (!Files.exists(taskDirectory)) {
                Files.createDirectories(taskDirectory);
            }
            for (MultipartFile file : files) {
                Path destinationFile = taskDirectory.resolve(
                                Paths.get(file.getOriginalFilename()))
                        .normalize().toAbsolutePath();
                Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
                fileRepository.save(new FileV3(file,taskId));
            }

            return loadAll(taskId);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store files.", e);
        }
    }


    @Override
    public List<FileInfoDTO> loadAll(Integer taskId) {
        return ListMapper.mapFileListToFileInfoDTOList(fileRepository.findAllByTaskId(taskId));
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

    public void delete(String filename, Integer taskId) {
        try {
            Path taskDirectory = rootLocation.resolve(taskId.toString());
            if (!Files.exists(taskDirectory) || !Files.isDirectory(taskDirectory)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task directory not found: " + taskId);
            }
            Path fileToDelete = taskDirectory.resolve(filename).normalize().toAbsolutePath();
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete); // Delete the file
                log.info("File deleted successfully: " + fileToDelete);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filename);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: " + filename, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file: " + filename, e);
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



    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not initialize storage", e);
        }
    }

}

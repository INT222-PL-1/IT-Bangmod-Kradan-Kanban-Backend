package sit.int221.itbkkbackend.v3.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
            for (MultipartFile mf : files) {
                Path destinationPath = taskDirectory.resolve(Paths.get(mf.getOriginalFilename())).normalize().toAbsolutePath();
                File file = destinationPath.toFile();
                Files.copy(mf.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                boolean isThumbnailCreated = false;
                if (canCreateThumbnail(file)) {
                    createThumbnail(file, taskDirectory, mf);
                    isThumbnailCreated = true;
                }
                FileInfoDTO fileInfoDto = new FileInfoDTO(fileRepository.save(new FileV3(mf, taskId)), taskId, boardId);
                if (isThumbnailCreated) {
                    addThumbnailPath(fileInfoDto);
                }
                fileList.add(fileInfoDto);
            }

            return fileList;

        } catch (IOException e) {
            log.error("Failed to store files.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store files.", e);
        }
    }

    @Override
    public List<FileInfoDTO> loadAll(TaskV3 task, String boardId) {
        List<FileInfoDTO> fileInfoDtoList = ListMapper.mapFileListToFileInfoDTOList(task.getFiles(), task.getId(), boardId);
        for (FileInfoDTO fileInfoDto : fileInfoDtoList) {
            addThumbnailPath(fileInfoDto);
        }
        return fileInfoDtoList;
    }

    @Override
    public List<FileInfoDTO> loadAll(Integer taskId, String boardId) {
        List<FileInfoDTO> fileInfoDtoList = ListMapper.mapFileListToFileInfoDTOList(fileRepository.findAllByTaskId(taskId), taskId, boardId);
        for (FileInfoDTO fileInfoDto : fileInfoDtoList) {
            addThumbnailPath(fileInfoDto);
        }
        return fileInfoDtoList;
    }

    @Override
    public Path load(String filename, Integer taskId) {
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
        return fileRepository.findByFileNameAndTaskId(taskId, filename);
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

        List<String> excludeNamesWithThumbnail = new LinkedList<>();
        if (excludeNames != null && !excludeNames.isEmpty()) {
            for (String excludeName : excludeNames) {
                excludeNamesWithThumbnail.add(excludeName);
                excludeNamesWithThumbnail.add("thumbnail_" + excludeName + ".jpg");
            }
        }

        try {
            Path taskDirectory = rootLocation.resolve(taskId.toString());

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(taskDirectory)) {
                for (Path path : stream) {
                    String fileName = path.getFileName().toString();
                    if (!excludeNamesWithThumbnail.contains(fileName)) {
                        Files.deleteIfExists(path);
                    }
                }
            }

            fileRepository.deleteFilesByTaskIdExcludingNames(taskId, excludeNames);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete files for task: " + taskId, e);
        } catch (Exception e) {
            log.error("Failed to delete files for task: " + taskId, e);
        }
    }

    private boolean canCreateThumbnail(File file) {
        try {
            if (!file.exists()) {
                log.error("File does not exist: " + file.getAbsolutePath());
                return false;
            }
    
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null && mimeType.startsWith("image/") && !mimeType.equals("image/svg+xml")) {
                BufferedImage image = ImageIO.read(file);
                if (image != null) {
                    return true;
                } else {
                    log.error("Failed to read image file: " + file.getAbsolutePath());
                }
            } else {
                log.error("Unsupported mime type: " + mimeType);
            }
        } catch (IOException e) {
            log.error("IOException occurred while checking thumbnail creation for file: " + file.getName(), e);
        } catch (Exception e) {
            log.error("Failed to create thumbnail for file: " + file.getName(), e);
        }
        return false;
    }

    private void createThumbnail(File file, Path taskDirectory, MultipartFile mf) {
        try {
            if (!file.exists()) {
                log.error("File does not exist: " + file.getAbsolutePath());
                return;
            }

            String mimeType = Files.probeContentType(file.toPath());

            log.info(mimeType);

            if (mimeType != null && mimeType.startsWith("image/")) {
                File thumbnailFile = taskDirectory.resolve("thumbnail_" + mf.getOriginalFilename()).toFile();
                Thumbnails.of(file)
                        .size(100, 100)
                        .outputFormat("jpg")
                        .toFile(thumbnailFile);

                log.info("Thumbnail created for file: " + mf.getOriginalFilename());
            } else {
                log.error("Unsupported mime type: " + mimeType);
            }
        } catch (IOException e) {
            log.error("IOException occurred while creating thumbnail for file: " + mf.getOriginalFilename(), e);
        } catch (Exception e) {
            log.error("Failed to create thumbnail for file: " + mf.getOriginalFilename(), e);
        }
    }

    private boolean hasThumbnail(Integer taskId, String fileName) {
        Path taskDirectory = rootLocation.resolve(taskId.toString());
        Path thumbnailPath = taskDirectory.resolve("thumbnail_" + fileName + ".jpg");
        return Files.exists(thumbnailPath);
    }

    private void addThumbnailPath(FileInfoDTO fileInfoDto) {
        if (hasThumbnail(fileInfoDto.getTaskId(), fileInfoDto.getName())) {
            fileInfoDto.setThumbnailPath(String.format(
                "/v3/boards/%s/tasks/%d/files/%s/thumbnail",
                fileInfoDto.getBoardId(),
                fileInfoDto.getTaskId(),
                fileInfoDto.getName()
            ));
        }
    }
}

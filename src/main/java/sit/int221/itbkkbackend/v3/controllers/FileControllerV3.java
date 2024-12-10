package sit.int221.itbkkbackend.v3.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;
import sit.int221.itbkkbackend.v3.repositories.TaskRepositoryV3;
import sit.int221.itbkkbackend.v3.services.StorageService;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:3000",
        "http://localhost:4173",
        "http://ip23pl1.sit.kmutt.ac.th:5173",
        "http://ip23pl1.sit.kmutt.ac.th:3000",
        "http://ip23pl1.sit.kmutt.ac.th:4173",
        "http://ip23pl1.sit.kmutt.ac.th",
        "http://intproj23.sit.kmutt.ac.th",
        "https://ip23pl1.sit.kmutt.ac.th:5173",
        "https://ip23pl1.sit.kmutt.ac.th:3000",
        "https://ip23pl1.sit.kmutt.ac.th:4173",
        "https://ip23pl1.sit.kmutt.ac.th",
        "https://intproj23.sit.kmutt.ac.th",
        "https://20.243.133.115"
},allowCredentials = "true")
@RestController
@RequestMapping("/v3/boards")
public class FileControllerV3 {
    private final StorageService storageService;
    private final TaskRepositoryV3 taskRepository;

    public FileControllerV3(StorageService storageService, TaskRepositoryV3 taskRepository) {
        this.storageService = storageService;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/{boardId}/tasks/{taskId}/files")
    public List<FileInfoDTO> listUploadedFiles(@PathVariable Integer taskId, @PathVariable String boardId, HttpServletRequest request) {
        if(!taskRepository.existsByIdAndBoardId(taskId, boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<FileInfoDTO> allFileDto = storageService.loadAll(taskId, boardId);
        for (FileInfoDTO fileDto : allFileDto) {
            fileDto.setSrcOrigin(request.getHeader("origin"));
        }
        return allFileDto;
    }

    @GetMapping("/{boardId}/tasks/{taskId}/files/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName,@PathVariable Integer taskId, @PathVariable String boardId) throws IOException {
        if(!taskRepository.existsByIdAndBoardId(taskId, boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        FileV3 data = storageService.loadAsData(fileName,taskId);
        if(data == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Resource file = storageService.loadAsResource(fileName,taskId);
        MediaType fileType = MediaType.parseMediaType(data.getType());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(fileType);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(file.contentLength());

        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @PostMapping("/{boardId}/tasks/{taskId}/files")
    public List<FileInfoDTO> handleFileUpload(@RequestParam("files") MultipartFile[] files, @PathVariable Integer taskId, @PathVariable String boardId) {
        if(!taskRepository.existsByIdAndBoardId(taskId, boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return storageService.store(files,taskId,boardId);
    }

}

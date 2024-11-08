package sit.int221.itbkkbackend.v3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;
import sit.int221.itbkkbackend.v3.repositories.TaskRepositoryV3;
import sit.int221.itbkkbackend.v3.services.StorageService;
import sit.int221.itbkkbackend.v3.services.TaskServiceV3;

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
        "https://intproj23.sit.kmutt.ac.th"
},allowCredentials = "true")
@RestController
@RequestMapping("/v3/boards")
public class FileControllerV3 {
    @Autowired
    private StorageService storageService;
    @Autowired
    private TaskRepositoryV3 taskRepository;

    @GetMapping("/{boardId}/tasks/{taskId}/files")
    public List<FileInfoDTO> listUploadedFiles(@PathVariable Integer taskId) {
        return storageService.loadAll(taskId);
    }

    @GetMapping("/{boardId}/tasks/{taskId}/files/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName,@PathVariable Integer taskId, @PathVariable String boardId) {
        if(taskRepository.existsByIdAndBoardId(taskId, boardId) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        FileV3 data = storageService.loadAsData(fileName,taskId);
        if(data == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Resource file = storageService.loadAsResource(fileName,taskId);
        MediaType fileType = MediaType.parseMediaType(data.getType());
        return ResponseEntity.ok().contentType(fileType).body(file);
    }

    @PostMapping("/{boardId}/tasks/{taskId}/files")
    public List<FileInfoDTO> handleFileUpload(@RequestParam("files") MultipartFile[] files, @PathVariable Integer taskId) {
        return storageService.store(files,taskId);
    }
}

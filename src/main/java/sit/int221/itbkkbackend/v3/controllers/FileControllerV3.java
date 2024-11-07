package sit.int221.itbkkbackend.v3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sit.int221.itbkkbackend.v3.dtos.FileInfoDTO;
import sit.int221.itbkkbackend.v3.entities.FileV3;
import sit.int221.itbkkbackend.v3.services.StorageService;

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

    @GetMapping("/{boardId}/tasks/{taskId}/files")
    public List<FileInfoDTO> listUploadedFiles(@PathVariable Integer taskId) {
        return storageService.loadAll(taskId);
    }

    @GetMapping("/{boardId}/tasks/{taskId}/files/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName,@PathVariable Integer taskId) {
        Resource file = storageService.loadAsResource(fileName,taskId);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        switch (extension) {
            case ".pdf":
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(file);
            case ".png":
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(file);
            case ".jpeg":
            case ".jpg":
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
            case ".gif":
            case ".jfif":
                return ResponseEntity.ok().contentType(MediaType.IMAGE_GIF).body(file);
            default:
                return ResponseEntity.ok().contentType(MediaType.ALL).body(file);
        }
    }

    @PostMapping("/{boardId}/tasks/{taskId}/files")
    public List<FileInfoDTO> handleFileUpload(@RequestParam("files") MultipartFile[] files, @PathVariable Integer taskId) {
        return storageService.store(files,taskId);
    }

    @DeleteMapping("/{boardId}/tasks/{taskId}/files/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable Integer taskId, @PathVariable String filename) {
        storageService.delete(filename, taskId);
        return ResponseEntity.ok("File deleted successfully: " + filename);
    }

}

package sit.int221.itbkkbackend.v3.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "task_attachment_v3", schema = "itb-kk")
public class FileV3 {
    @EmbeddedId
    private FileKey fileKey;
    private String type;
    private Long size;

    public FileV3(MultipartFile file) {
        this.fileKey.setName(file.getOriginalFilename());
        this.type = file.getContentType();
        this.size = file.getSize();
    }

    public FileV3(MultipartFile file, Integer taskId) {
        this.fileKey = new FileKey(file.getOriginalFilename(), taskId);
        this.type = file.getContentType();
        this.size = file.getSize();
    }

    public FileV3(String name, Integer taskId, String type, Long size) {
        this.fileKey = new FileKey(name, taskId);
        this.type = type;
        this.size = size;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class FileKey implements Serializable {
        @Column(name = "name")
        private String name;

        @Column(name = "task_id")
        private Integer taskId;

    }

}

package sit.int221.itbkkbackend.v3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.v3.entities.FileV3;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDTO {
    private String name;
    private String type;
    private Long size;
    private String path;

    @JsonIgnore
    private Integer taskId;

    @JsonIgnore
    private String boardId;

    public FileInfoDTO(FileV3 file, Integer taskId, String boardId) {
        this.name = file.getFileKey().getName();
        this.type = file.getType();
        this.size = file.getSize();
        this.boardId = boardId;
        this.taskId = taskId;
        this.path = String.format(
            "/v3/boards/%s/tasks/%d/files/%s",
            boardId,
            taskId,
            this.name
        );
    }
}

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
    private String url;

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
        this.url = formatUrl("https://intproj23.sit.kmutt.ac.th/pl1/api/", boardId, taskId);
    }

    private String formatUrl(String origin, String boardId, Integer taskId) {
        return String.format(
            "%s/v3/boards/%s/tasks/%d/files/%s",
            origin,
            boardId,
            taskId,
            this.name
        );
    }

    public void setSrcOrigin(String origin) {
        this.url = formatUrl(origin, this.boardId, this.taskId);
    }
}

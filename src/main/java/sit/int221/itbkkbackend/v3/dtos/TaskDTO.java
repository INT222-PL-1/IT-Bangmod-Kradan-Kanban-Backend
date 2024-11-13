package sit.int221.itbkkbackend.v3.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import sit.int221.itbkkbackend.v3.entities.StatusV3;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Integer id;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(max = 100)
    private String title;
    @Size(max = 500 )
    private String description;
    @Size(max = 30)
    private String assignees;
    @JsonIgnore
    private StatusV3 status;
    private Integer statusId;
    private String boardId;

    private List<String> attachments;

    public void setBoardId(String boardId) { this.boardId = boardId; }


    public Integer getStatusId() { return status == null ? statusId :  status.getId(); }

    public void setAssignees(String assignees) {
        this.assignees = assignees == null ||  assignees.isBlank() ? null : assignees.trim();
    }
    public void setDescription(String description){
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public void setTitle(String title){
        if (title == null) {
            this.title = null;
        } else if (title.isBlank()) {
            this.title = "";
        } else {
            this.title = title.trim();
        }
    }


}
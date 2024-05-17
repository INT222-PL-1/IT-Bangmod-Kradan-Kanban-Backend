package sit.int221.itbkkbackend.v2.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SaveTaskDTO {
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 1,max = 100 )
    private String title;
    public void setTitle(String title){
        this.title = title.trim();
    }
    @Size(max = 500 )
    private String description;
    public void setDescription(String description){
        this.description = description == null || description.isBlank() ? null : description.trim();
    }
    @Size(max = 30)
    private String assignees;
    public void setAssignees(String assignees) {
        this.assignees = assignees == null ||  assignees.isBlank() ? null : assignees.trim();
    }

    private Integer status;
    private Integer statusId;
    private Integer boardId;

}

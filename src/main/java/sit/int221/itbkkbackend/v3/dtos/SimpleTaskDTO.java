package sit.int221.itbkkbackend.v3.dtos;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.v3.entities.StatusV3;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTaskDTO {
    private Integer id;
    @NotNull
    @Size(max = 100)
    private String title;

    @Size(max = 30)
    private String assignees;
    private StatusV3 status;
    private String boardId;

    public void setTitle(String title) {
        this.title = title.trim();
    }
    public void setAssignees(String assignees) {
        this.assignees = assignees == null ? assignees : assignees.trim();
    }

}

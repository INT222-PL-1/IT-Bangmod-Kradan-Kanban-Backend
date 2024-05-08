package sit.int221.itbkkbackend.v2.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.v2.entities.StatusV2;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTaskDTO {
    @NotNull
    private Integer id;
    @NotNull
    @Size(max = 100)
    private String title;
    public void setTitle(String title) {
        this.title = title.trim();
    }

    @Size(max = 30)
    private String assignees;
    public void setAssignees(String assignees) {
        this.assignees = assignees == null ? assignees : assignees.trim();
    }
    @JsonIgnore
    private StatusV2 status;

    private Integer statusId;

    public Integer getStatusId() {
        return status == null ? statusId :  status.getId();
    }


}

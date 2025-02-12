package sit.int221.itbkkbackend.v1.dtos;

import lombok.*;
import jakarta.validation.constraints.*;
import sit.int221.itbkkbackend.v1.entities.StatusV1;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTaskDTO {
    @NotNull
    private Integer id;
    @NotNull
    @Size(min = 1,max = 100)
    private String title;
    public String getTitle() {
        return title.trim();
    }

    @Size(min = 1 , max = 30)
    private String assignees;
    public String getAssignees() {
        return assignees == null ? assignees : assignees.trim();
    }
    private StatusV1 status;
}

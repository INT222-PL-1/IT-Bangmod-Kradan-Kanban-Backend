package sit.int221.itbkkbackend.dtos;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.entities.Status;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Integer id;
    @NotNull
    @Max(100)
    private String title;
    @NotEmpty
    @Max(500)
    private String description;
    public String getDescription(){
        return description.trim();
    }
    @NotEmpty
    @Max(30)
    private String assignees;
    public String getAssignees() {
        return assignees.trim();
    }
    @NotNull
    private Status status;
    @NotNull
    private ZonedDateTime createdOn;
    @NotNull
    private ZonedDateTime updatedOn;

}

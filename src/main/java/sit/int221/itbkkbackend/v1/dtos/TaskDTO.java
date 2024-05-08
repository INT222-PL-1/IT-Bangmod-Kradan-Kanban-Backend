package sit.int221.itbkkbackend.v1.dtos;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.v1.entities.Status;

import java.time.ZonedDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Integer id;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 1,max = 100 )
    private String title;
    public String getTitle(){
        return title.trim();
    }
    @Size(min = 1,max = 500 )
    private String description;
    public String getDescription(){
        return description == null || description.isBlank() ? null : description.trim();
    }
    @Size(min = 1 , max = 30)
    private String assignees;
    public String getAssignees() {
        return assignees == null ||  assignees.isBlank() ? null : assignees.trim();
    }
    private Status status;
    public Status getStatus(){
        return  status == null  ? Status.NO_STATUS : status;
    }
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;

}

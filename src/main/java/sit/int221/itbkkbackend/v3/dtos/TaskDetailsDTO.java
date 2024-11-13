package sit.int221.itbkkbackend.v3.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.v3.entities.StatusV3;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailsDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private StatusV3 status;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;

    private List<FileInfoDTO> attachments;
}

package sit.int221.itbkkbackend.v2.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sit.int221.itbkkbackend.v2.entities.BoardV2;
import sit.int221.itbkkbackend.v2.entities.StatusV2;

import java.time.ZonedDateTime;
@Getter
@Setter
public class TaskDetailsDTO {

    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private StatusV2 status;
    private Integer boardId;
    private Integer statusId;
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}

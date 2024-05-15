package sit.int221.itbkkbackend.v2.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import sit.int221.itbkkbackend.v2.entities.TaskV2;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetStatusMaxTasksDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private List<TaskV2> tasks;

    private Integer noOfTasks;
    private Boolean limitMaximumTasks;
}

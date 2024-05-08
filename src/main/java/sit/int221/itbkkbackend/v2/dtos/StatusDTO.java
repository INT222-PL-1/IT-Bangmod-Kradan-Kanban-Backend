package sit.int221.itbkkbackend.v2.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sit.int221.itbkkbackend.v2.entities.TaskV2;

import java.util.List;

@Getter
@Setter
public class StatusDTO {
    private Integer id;
    @NotEmpty
    @Size(max=50)
    private String name;
    @Size(max=200)
    private String description;
    private String color;
    @JsonIgnore
    private List<TaskV2> tasks;

    private Integer count;

    public Integer getCount() {
        return tasks == null ? 0 : tasks.size();
    }

    public void setName(String name) {
        this.name = name.trim();
    }
    public void setDescription(String description) {
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public void setColor(String color) {
        this.color = color == null || color.isBlank() ? null : color.trim();
    }
}

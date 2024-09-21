package sit.int221.itbkkbackend.v3.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class StatusDTO {
    private Integer id;
    @NotNull
    @NotEmpty
    @Size(max=50)
    private String name;
    @Size(max=200)
    private String description;
    private String color;

    @JsonIgnore
    private String boardId;
    private Integer count;
    private Boolean isPredefined;

    public void setName(String name) {
        this.name = name == null ? null : name.isBlank() ? "" :  name.trim();
    }

    public void setDescription(String description) {
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public void setColor(String color) {
        this.color = color == null || color.isBlank() ? null : color.trim();
    }
}

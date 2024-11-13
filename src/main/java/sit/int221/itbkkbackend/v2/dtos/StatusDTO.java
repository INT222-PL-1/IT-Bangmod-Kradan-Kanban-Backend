package sit.int221.itbkkbackend.v2.dtos;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
    private Integer boardId;
    private Integer count;
    private Boolean isFixedStatus;

    public void setName(String name) {
        if (name == null) {
            this.name = null;
        } else if (name.isBlank()) {
            this.name = "";
        } else {
            this.name = name.trim();
        }
    }

    public void setDescription(String description) {
        this.description = description == null || description.isBlank() ? null : description.trim();
    }

    public void setColor(String color) {
        this.color = color == null || color.isBlank() ? null : color.trim();
    }
}

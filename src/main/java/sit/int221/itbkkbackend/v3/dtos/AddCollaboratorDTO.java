package sit.int221.itbkkbackend.v3.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddCollaboratorDTO {
    private String email;
    @Pattern(regexp = "READ|WRITE" ,message = "must be either READ or WRITE")
    private String accessRight;
}

package sit.int221.itbkkbackend.v3.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCollaboratorDTO {
    @NotEmpty
    @Pattern(regexp = "READ|WRITE" ,message = "must be either READ or WRITE")
    private String accessRight;
}

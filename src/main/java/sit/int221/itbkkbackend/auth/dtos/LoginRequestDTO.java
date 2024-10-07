package sit.int221.itbkkbackend.auth.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Size(max = 50)
    @NotEmpty
    private String userName;
    @Size(max = 14)
    @NotEmpty
    private String password;
}
package sit.int221.itbkkbackend.v3.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.auth.UsersDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardDTO {
    private String id;
    @NotEmpty
    @Size(max = 120)
    private String name;
    private Boolean isLimitTasks;
    private Integer taskLimitPerStatus;
    private List<StatusDTO> exceedLimitStatus;
    private UsersDTO owner;
}

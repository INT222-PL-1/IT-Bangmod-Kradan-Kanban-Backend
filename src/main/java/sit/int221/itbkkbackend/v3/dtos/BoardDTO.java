package sit.int221.itbkkbackend.v3.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.auth.dtos.UsersDTO;

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
    private Boolean isTaskLimitEnabled;
    private Integer taskLimitPerStatus;
    @Pattern(regexp = "PUBLIC|PRIVATE" ,message = "must be either PUBLIC or PRIVATE")
    private String visibility;
    private List<StatusDTO> exceedLimitStatus;
    private String accessRight;
    private String inviteStatus;
    private UsersDTO owner;

    public BoardDTO(String id, String name, Boolean isTaskLimitEnabled, Integer taskLimitPerStatus, String visibility,String accessRight, String oid, String ownerName) {
        this.id = id;
        this.name = name;
        this.isTaskLimitEnabled = isTaskLimitEnabled;
        this.taskLimitPerStatus = taskLimitPerStatus;
        this.visibility = visibility;
        this.accessRight = accessRight;
        this.owner = new UsersDTO(oid,ownerName);
    }

    public BoardDTO(String id, String name, Boolean isTaskLimitEnabled, Integer taskLimitPerStatus, String visibility,String accessRight, String inviteStatus,String oid, String ownerName) {
        this.id = id;
        this.name = name;
        this.isTaskLimitEnabled = isTaskLimitEnabled;
        this.taskLimitPerStatus = taskLimitPerStatus;
        this.visibility = visibility;
        this.accessRight = accessRight;
        this.inviteStatus = inviteStatus;
        this.owner = new UsersDTO(oid,ownerName);
    }
}

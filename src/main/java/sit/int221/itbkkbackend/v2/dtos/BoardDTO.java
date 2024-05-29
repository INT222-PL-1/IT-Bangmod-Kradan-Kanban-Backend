package sit.int221.itbkkbackend.v2.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardDTO {
    private Integer id;
    private Boolean isLimitTasks;
    private Integer taskLimitPerStatus;
    private List<StatusDTO> exceedLimitStatus;
}

package sit.int221.itbkkbackend.v2.dtos;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDTO {

    private Integer id;
    private Boolean isLimitTasks;
    private Integer taskLimitPerStatus;
}

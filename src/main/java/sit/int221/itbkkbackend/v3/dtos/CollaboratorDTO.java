package sit.int221.itbkkbackend.v3.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollaboratorDTO {
    private String oid;
    private String name;
    private String email;
    private String accessRight;
}

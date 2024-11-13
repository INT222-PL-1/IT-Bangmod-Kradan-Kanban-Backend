package sit.int221.itbkkbackend.v3.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
@Getter
@Setter
public class CollaboratorDetailsDTO {
    private String oid;
    private String name;
    private String email;
    private String accessRight;
    private String inviteStatus;
    private ZonedDateTime addedOn;
    private String ownerName;
    private String boardName;

    public CollaboratorDetailsDTO(String oid, String name, String email, String accessRight, String inviteStatus, ZonedDateTime addedOn) {
        this.oid = oid;
        this.name = name;
        this.email = email;
        this.accessRight = accessRight;
        this.inviteStatus = inviteStatus;
        this.addedOn = addedOn;
    }
}

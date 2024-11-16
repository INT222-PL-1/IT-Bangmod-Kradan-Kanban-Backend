package sit.int221.itbkkbackend.auth.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAuthority {
    OWNER("OWNER"),
    COLLABORATOR("COLLABORATOR"),
    PUBLIC_ACCESS("PUBLIC_ACCESS");
    
    private final String authority;
}

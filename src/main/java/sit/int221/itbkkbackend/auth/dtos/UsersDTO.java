package sit.int221.itbkkbackend.auth.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsersDTO {
    private String oid;
    private String username;
    private String name;

    public UsersDTO(String oid, String name){
        this.name = name;
        this.oid = oid;
    }

}

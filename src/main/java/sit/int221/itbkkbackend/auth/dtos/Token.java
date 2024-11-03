package sit.int221.itbkkbackend.auth.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token {
    private String access_token;
    private String refresh_token;

    public Token(String access_token){
        this.access_token = access_token;
    }
}

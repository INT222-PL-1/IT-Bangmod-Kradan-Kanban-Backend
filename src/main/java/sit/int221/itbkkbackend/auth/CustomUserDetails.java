package sit.int221.itbkkbackend.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {
    private String oid;
    private String name;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,String oid, String name) {
        super(username, password, authorities);
        this.oid = oid;
        this.name = name;
    }
}

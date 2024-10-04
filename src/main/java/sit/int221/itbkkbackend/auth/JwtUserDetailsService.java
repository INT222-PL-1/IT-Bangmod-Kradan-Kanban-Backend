package sit.int221.itbkkbackend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BoardRepositoryV3 boardRepositoryV3;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userName + " does not exist !!");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        };
        roles.add(grantedAuthority);
        return new User(userName,user.getPassword(),roles);
    }

    public UserDetails loadUserByUsername(String userName,String boardId) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userName + " does not exist !!");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        GrantedAuthority grantedAuthority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        };
        if(boardId == null || boardRepositoryV3.existsBoardV3sByIdAndVisibility(boardId ,"PUBLIC")){
            roles.add(new SimpleGrantedAuthority("public_access"));
        }
        if(boardId == null || boardRepositoryV3.existsBoardV3sByIdAndOwnerOid(boardId,user.getOid())){
            roles.add(new SimpleGrantedAuthority("owner"));
        }
        roles.add(grantedAuthority);
        return new User(userName,user.getPassword(),roles);
    }
}


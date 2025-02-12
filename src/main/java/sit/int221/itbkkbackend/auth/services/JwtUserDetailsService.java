package sit.int221.itbkkbackend.auth.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.auth.CustomUserDetails;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.v3.repositories.BoardPermissionRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final BoardRepositoryV3 boardRepository;
    private final BoardPermissionRepositoryV3 boardPermissionRepository;

    public JwtUserDetailsService(UsersRepository usersRepository, BoardRepositoryV3 boardRepository, BoardPermissionRepositoryV3 boardPermissionRepository) {
        this.usersRepository = usersRepository;
        this.boardRepository = boardRepository;
        this.boardPermissionRepository = boardPermissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userName + " does not exist !!");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        return new User(userName,user.getPassword(),roles);
    }

    public CustomUserDetails loadUserByUsername(String userName, String boardId) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userName + " does not exist !!");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        if (boardId == null || boardRepository.existsBoardV3sByIdAndVisibility(boardId, "PUBLIC")) {
            roles.add(new SimpleGrantedAuthority("PUBLIC_ACCESS"));
        }
        String permission = (boardId != null) ? boardPermissionRepository.getAccessRightByBoardIdAndOid(boardId, user.getOid()) : null;
        if (permission != null) {
            if (permission.equals("OWNER")) {
                roles.add(new SimpleGrantedAuthority(permission));
            } else if (permission.equals("READ") || permission.equals("WRITE")){
                roles.add(new SimpleGrantedAuthority("COLLABORATOR"));
                roles.add(new SimpleGrantedAuthority(permission));
            }
        }

        return new CustomUserDetails(userName, user.getPassword(), roles, user.getOid(), user.getName());
    }
}


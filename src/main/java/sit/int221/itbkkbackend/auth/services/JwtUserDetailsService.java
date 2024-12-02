package sit.int221.itbkkbackend.auth.services;

import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
import sit.int221.itbkkbackend.auth.utils.JwksTokenUtil;
import sit.int221.itbkkbackend.config.MicrosoftGraphConfig;
import sit.int221.itbkkbackend.v3.entities.UserV3;
import sit.int221.itbkkbackend.v3.repositories.BoardPermissionRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;
import sit.int221.itbkkbackend.v3.repositories.UserRepositoryV3;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final UserRepositoryV3 localUserRepository;
    private final BoardRepositoryV3 boardRepository;
    private final BoardPermissionRepositoryV3 boardPermissionRepository;
    private final JwksTokenUtil jwksTokenUtil;
    private final ModelMapper mapper;

    public JwtUserDetailsService(UsersRepository usersRepository, UserRepositoryV3 localUserRepository, BoardRepositoryV3 boardRepository, BoardPermissionRepositoryV3 boardPermissionRepository,JwksTokenUtil jwksTokenUtil,ModelMapper mapper) {
        this.usersRepository = usersRepository;
        this.localUserRepository = localUserRepository;
        this.boardRepository = boardRepository;
        this.boardPermissionRepository = boardPermissionRepository;
        this.jwksTokenUtil = jwksTokenUtil;
        this.mapper = mapper;
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
        List<GrantedAuthority> roles = grantedAuthoritiesList(user.getOid(),boardId);
        return new CustomUserDetails(userName, user.getPassword(), roles, user.getOid(), user.getName());
    }

    public CustomUserDetails loadUserByMicrosoftToken(String token,String boardId){
        String oid = jwksTokenUtil.getOidFromToken(token);
        GraphServiceClient graphClient = MicrosoftGraphConfig.getGraphClient(token);
        com.microsoft.graph.models.User user = graphClient.users().byUserId(oid).get();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with Id"  + " does not exist !!");
        }
        UserV3 localUser = null;
        if(localUserRepository.existsById(oid)){
            localUser = localUserRepository.findByOid(oid);
            System.out.println(localUser.toString());
        } else if (usersRepository.existsById(oid)) {
            Users sharedUser = usersRepository.findByOid(oid);
            localUser = localUserRepository.save(mapper.map(sharedUser,UserV3.class));
        } else {
            UserV3 newUser = new UserV3();
            newUser.setName(user.getDisplayName());
            newUser.setOid(user.getId());
            newUser.setEmail(user.getMail());
            newUser.setUsername("MS_USER");
            localUser = localUserRepository.save(newUser);
        }
        List<GrantedAuthority> roles = grantedAuthoritiesList(oid,boardId);
        return new CustomUserDetails(localUser.getUsername(),"",roles,oid,localUser.getName());
    }

    public List<GrantedAuthority> grantedAuthoritiesList(String oid, String boardId){
        List<GrantedAuthority> roles = new ArrayList<>();
        if (boardId == null || boardRepository.existsBoardV3sByIdAndVisibility(boardId, "PUBLIC")) {
            roles.add(new SimpleGrantedAuthority("PUBLIC_ACCESS"));
        }
        String permission = (boardId != null) ? boardPermissionRepository.getAccessRightByBoardIdAndOid(boardId, oid) : null;
        if (permission != null) {
            if (permission.equals("OWNER")) {
                roles.add(new SimpleGrantedAuthority(permission));
            } else if (permission.equals("READ") || permission.equals("WRITE")){
                roles.add(new SimpleGrantedAuthority("COLLABORATOR"));
                roles.add(new SimpleGrantedAuthority(permission));
            }
        }
        return roles;
    }

}


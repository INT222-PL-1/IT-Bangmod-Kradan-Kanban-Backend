package sit.int221.itbkkbackend.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import sit.int221.itbkkbackend.exceptions.AuthorizationFilterException;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String oid = null;
        String jwtToken = null;
        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    oid = jwtTokenUtil.getOidFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    request.setAttribute("errorType","JWT Token is not well formed");
                } catch (ExpiredJwtException e) {
                    request.setAttribute("errorType","JWT Token expired");
                } catch (SignatureException e){
                    request.setAttribute("errorType","JWT token has been tampered with");
                }
            } else {
                request.setAttribute("errorType","JWT Token does not begin with Bearer String");
            }
        }else {
            request.setAttribute("errorType", "Access Token Required");
        }
        if (oid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = usersRepository.findByOid(oid).getUsername();
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(jwtToken)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }

        }

        chain.doFilter(request, response);
    }
}


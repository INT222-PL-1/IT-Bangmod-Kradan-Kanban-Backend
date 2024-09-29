package sit.int221.itbkkbackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import sit.int221.itbkkbackend.exceptions.AuthorizationFilterException;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;
import sit.int221.itbkkbackend.utils.UriExtractor;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private BoardRepositoryV3 boardRepository;
    @Autowired
    private UriExtractor uriExtractor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String boardId = uriExtractor.getBoardId(request);
        if (boardId != null) {
//            if(!boardRepository.existsById(boardId)){
//                notFoundResponseHandler(String.format("Board Id %s not found",boardId),request,response);
//                return;
//            }
            request.setAttribute("boardId",boardId);
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String oid = null;
        String jwtToken = null;
        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    oid = jwtTokenUtil.getOidFromToken(jwtToken, JwtTokenUtil.TokenType.ACCESS);
                } catch (IllegalArgumentException | MalformedJwtException e) {
                    request.setAttribute("errorType", ErrorType.TOKEN_MALFORMED);
                } catch (ExpiredJwtException e) {
                    request.setAttribute("errorType", ErrorType.TOKEN_EXPIRED);
                } catch (SignatureException e){
                    request.setAttribute("errorType", ErrorType.TOKEN_TAMPERED);
                }
            } else {
                request.setAttribute("errorType", ErrorType.TOKEN_NOT_BEGIN_WITH_BEARER);
            }
        }

        if (oid != null) {
            Users user = usersRepository.findByOid(oid);
            if(user == null){
                request.setAttribute("errorType", ErrorType.USER_NOT_FOUND);
            } else {
                String username = user.getUsername();
                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username,boardId == null ? null :  request.getAttribute("boardId").toString());
                if (jwtTokenUtil.validateToken(jwtToken, JwtTokenUtil.TokenType.ACCESS)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }

        if(boardId != null
                && !boardRepository.existsById(boardId)
                && ( SecurityContextHolder.getContext().getAuthentication() != null || HttpMethod.GET.matches(request.getMethod()))
                ){
            notFoundResponseHandler(String.format("Board Id %s not found",boardId),request,response);
            return;
        }

        chain.doFilter(request, response);
    }



    private void notFoundResponseHandler(String message , HttpServletRequest request, HttpServletResponse response) throws IOException {
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        ErrorResponse error = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                message,
                request.getRequestURI()
        );
        response.setContentType("application/json");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        objectMapper.writeValue(response.getWriter(), error);
    }
}


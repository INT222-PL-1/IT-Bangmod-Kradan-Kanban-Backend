package sit.int221.itbkkbackend.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.itbkkbackend.auth.*;
import sit.int221.itbkkbackend.auth.entities.Users;
import sit.int221.itbkkbackend.auth.repositories.UsersRepository;
import sit.int221.itbkkbackend.auth.services.JwtUserDetailsService;
import sit.int221.itbkkbackend.auth.utils.JwtTokenUtil;
import sit.int221.itbkkbackend.auth.utils.enums.ErrorType;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;
import sit.int221.itbkkbackend.utils.UriExtractor;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;

import java.io.IOException;
import java.sql.Timestamp;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String ERROR_TYPE = "errorType";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersRepository usersRepository;
    private final BoardRepositoryV3 boardRepository;
    private final UriExtractor uriExtractor;

    public JwtAuthFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil, UsersRepository usersRepository, BoardRepositoryV3 boardRepository, UriExtractor uriExtractor) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.usersRepository = usersRepository;
        this.boardRepository = boardRepository;
        this.uriExtractor = uriExtractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String boardId = uriExtractor.getBoardId(request);
        if (boardId != null) {
            request.setAttribute("boardId",boardId);
        }

        final String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userOid = null;
        if (requestTokenHeader != null) {
            jwtToken = extractJwtToken(requestTokenHeader, request);
            if (jwtToken != null) {
                userOid = getUserOidFromToken(jwtToken);
            }
        }

        if (userOid != null) {
            authenticateUser(jwtToken, userOid, boardId, request);
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
    
    private String extractJwtToken(String requestTokenHeader, HttpServletRequest request) {
        if (requestTokenHeader.startsWith("Bearer ")) {
            try {
                return requestTokenHeader.substring(7);
            } catch (IllegalArgumentException | MalformedJwtException e) {
                request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_MALFORMED);
            } catch (ExpiredJwtException e) {
                request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_EXPIRED);
            } catch (SignatureException e){
                request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_TAMPERED);
            }
        }
        return null;
    }

    private String getUserOidFromToken(String jwtToken) {
        try {
            return jwtTokenUtil.getOidFromToken(jwtToken, JwtTokenUtil.TokenType.ACCESS);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void authenticateUser(String jwtToken, String userOid, String boardId, HttpServletRequest request) {
        Users user = usersRepository.findByOid(userOid);
        if(user == null){
            request.setAttribute(ERROR_TYPE, ErrorType.USER_NOT_FOUND);
        } else {
            String username = user.getUsername();
            CustomUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username,boardId == null ? null :  request.getAttribute("boardId").toString());
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, JwtTokenUtil.TokenType.ACCESS))) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()));
            }
        }
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


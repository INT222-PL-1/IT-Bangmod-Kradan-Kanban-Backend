package sit.int221.itbkkbackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AnonymousAuthFilter extends OncePerRequestFilter {
    @Autowired
    private BoardRepositoryV3 boardRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            Pattern pattern = Pattern.compile(".*/boards/([^/]+)(?:/[^/]+.*)?");
            Matcher matcher = pattern.matcher(request.getRequestURI());
            List<GrantedAuthority> authorities = new LinkedList<>();

            if (matcher.matches()) {
                String boardId = matcher.group(1);
                if(!boardRepository.existsById(boardId)){
                    notFoundResponseHandler(String.format("Board Id %s not found",boardId),request,response);
                    return;
                }
                request.setAttribute("boardId",boardId);
                // check board if visibility PUBLIC or PRIVATE
                authorities.add(new SimpleGrantedAuthority(boardRepository.existsBoardV3sByIdAndVisibility(boardId, "PUBLIC") ? "public_access"  : "anonymous"));
            } else {
                authorities.add(new SimpleGrantedAuthority("public_access"));
            }
        if(HttpMethod.GET.matches(request.getMethod())) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, authorities));
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

package sit.int221.itbkkbackend.auth.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import sit.int221.itbkkbackend.auth.utils.enums.ErrorType;
import sit.int221.itbkkbackend.v3.repositories.BoardRepositoryV3;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class AnonymousAuthFilter extends OncePerRequestFilter {
    private static final String ERROR_TYPE = "errorType";
    private final BoardRepositoryV3 boardRepository;

    public AnonymousAuthFilter(BoardRepositoryV3 boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //PUBLIC ACCESS CHECK
        if(HttpMethod.GET.matches(request.getMethod()) && SecurityContextHolder.getContext().getAuthentication() == null && request.getAttribute(ERROR_TYPE) == null) {
            List<GrantedAuthority> authorities = new LinkedList<>();
            String boardId = (String) request.getAttribute("boardId");
            if(Boolean.TRUE.equals(boardRepository.existsBoardV3sByIdAndVisibility(boardId, "PUBLIC"))){
                authorities.add(new SimpleGrantedAuthority("PUBLIC_ACCESS"));
            } else {
                authorities.add(new SimpleGrantedAuthority("anonymous"));
                request.setAttribute(ERROR_TYPE, ErrorType.UNAUTHORIZED_PRIVATE_ACCESS);
            }
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(null, null, authorities));
        }

        //PUBLIC UPDATE CHECK
        if (!HttpMethod.GET.matches(request.getMethod()) && SecurityContextHolder.getContext().getAuthentication() == null && request.getAttribute(ERROR_TYPE) == null){
            request.setAttribute(ERROR_TYPE, ErrorType.UNAUTHORIZED_UPDATE);
        }
        log.info("anoy filter");
        chain.doFilter(request, response);
    }


}

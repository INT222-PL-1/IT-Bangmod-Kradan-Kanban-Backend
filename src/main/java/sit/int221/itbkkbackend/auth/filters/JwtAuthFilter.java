package sit.int221.itbkkbackend.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import sit.int221.itbkkbackend.auth.utils.JwksTokenUtil;
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
    private final String tenantId;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersRepository usersRepository;
    private final BoardRepositoryV3 boardRepository;
    private final JwksTokenUtil jwksTokenUtil;
    private final UriExtractor uriExtractor;

    public JwtAuthFilter(JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil, UsersRepository usersRepository, BoardRepositoryV3 boardRepository, UriExtractor uriExtractor, JwksTokenUtil jwksTokenUtil, @Value("${tenant.id}") String tenantId) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.usersRepository = usersRepository;
        this.boardRepository = boardRepository;
        this.jwksTokenUtil = jwksTokenUtil;
        this.uriExtractor = uriExtractor;
        this.tenantId = tenantId;
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
                if(isTokenSourceFromMicrosoft(jwtToken,request)){
                    System.out.println("ms");
                    userOid = getUserOidFromMicrosoftToken(jwtToken,request);
                    authenticateMicrosoftUser(jwtToken, userOid, boardId, request);
                }else {
                    System.out.println("own");
                    userOid = getUserOidFromOwnToken(jwtToken,request);
                    authenticateOwnUser(jwtToken, userOid, boardId, request);
                }
            }
        }

        // if request URI include boardId and boardId does not exist throw 404
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
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private String getUserOidFromOwnToken(String jwtToken,HttpServletRequest request) {
        try {
            return jwtTokenUtil.getOidFromToken(jwtToken, JwtTokenUtil.TokenType.ACCESS);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_MALFORMED);
        } catch (ExpiredJwtException e) {
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_EXPIRED);
        } catch (SignatureException e){
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_TAMPERED);
        }
        return null;
    }

    private String getUserOidFromMicrosoftToken(String jwtToken,HttpServletRequest request) {
        try {
            return jwksTokenUtil.getOidFromToken(jwtToken);
        } catch (IllegalArgumentException | MalformedJwtException e) {
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_MALFORMED);
        } catch (ExpiredJwtException e) {
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_EXPIRED);
        } catch (SignatureException e){
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_TAMPERED);
        }
        return null;
    }

    private void authenticateOwnUser(String jwtToken, String userOid, String boardId, HttpServletRequest request) {
        if(userOid == null){
            return;
        }
        Users user = usersRepository.findByOid(userOid);
        if(user == null){
            request.setAttribute(ERROR_TYPE, ErrorType.USER_NOT_FOUND);
        } else {
            String username = user.getUsername();
            CustomUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username,boardId == null ? null :  request.getAttribute("boardId").toString());
            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, JwtTokenUtil.TokenType.ACCESS))) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    }

    private void authenticateMicrosoftUser(String jwtToken, String userOid, String boardId, HttpServletRequest request) {
        if(userOid == null){
            return;
        }
        CustomUserDetails userDetails = this.jwtUserDetailsService.loadUserByMicrosoftToken(jwtToken,boardId == null ? null :  request.getAttribute("boardId").toString());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    private Boolean isTokenSourceFromMicrosoft(String token,HttpServletRequest request){
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if(claims.getIssuer().contains(tenantId)){
                return true;
            }
        }catch (Exception e){
            request.setAttribute(ERROR_TYPE, ErrorType.TOKEN_MALFORMED);
        }
        return false;
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


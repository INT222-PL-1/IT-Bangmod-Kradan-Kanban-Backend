package sit.int221.itbkkbackend.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import sit.int221.itbkkbackend.auth.utils.enums.ErrorType;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;

import java.io.IOException;
import java.sql.Timestamp;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        ErrorType errorType  = (ErrorType) request.getAttribute("errorType");
        if(errorType == null){
            errorType = ErrorType.AUTHENTICATION_FAILED;
        }
        ErrorResponse error = new ErrorResponse(
                errorType,
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                errorType.getMessage(),
                request.getRequestURI()
        );
        response.setContentType("application/json");
        response.setStatus(401);
        objectMapper.writeValue(response.getWriter(), error);


    }
}

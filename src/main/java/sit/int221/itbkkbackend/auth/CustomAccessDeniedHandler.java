package sit.int221.itbkkbackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import sit.int221.itbkkbackend.auth.utils.enums.ErrorType;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;

import java.io.IOException;
import java.sql.Timestamp;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        ErrorType errorMessage  = (ErrorType) request.getAttribute("errorType");
        if(errorMessage == null){
            errorMessage = ErrorType.AUTHORIZATION_FAILED;
        }
        ErrorResponse error = new ErrorResponse(
                errorMessage,
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                errorMessage.getMessage(),
                request.getRequestURI()
        );
        response.setContentType("application/json");
        response.setStatus(403);
        objectMapper.writeValue(response.getWriter(), error);
    }
}

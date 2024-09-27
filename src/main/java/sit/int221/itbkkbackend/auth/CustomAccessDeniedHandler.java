package sit.int221.itbkkbackend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.web.access.AccessDeniedHandler;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;

import java.io.IOException;
import java.sql.Timestamp;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        String errorMessage  = (String) request.getAttribute("errorType");
        if(errorMessage == null){
            errorMessage = "Authorization Failed , Please Try Again";
        }
        ErrorResponse error = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                errorMessage,
                request.getRequestURI()
        );
        response.setContentType("application/json");
        response.setStatus(403);
        objectMapper.writeValue(response.getWriter(), error);
    }
}

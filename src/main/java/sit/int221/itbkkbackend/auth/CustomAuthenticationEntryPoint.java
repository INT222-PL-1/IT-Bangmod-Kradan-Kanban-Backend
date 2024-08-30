package sit.int221.itbkkbackend.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sit.int221.itbkkbackend.controllers.GlobalExceptionHandler;
import sit.int221.itbkkbackend.exceptions.AuthorizationFilterException;
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
        String errorMessage  = (String) request.getAttribute("errorType");
        if(errorMessage == null){
            errorMessage = "Authentication Failed , Please Try Again";
        }
        ErrorResponse error = new ErrorResponse(

                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                 errorMessage,
                request.getRequestURI()
        );
        response.setContentType("application/json");
        response.setStatus(401);
        objectMapper.writeValue(response.getWriter(), error);

//        request.setAttribute(RequestDispatcher.ERROR_EXCEPTION,
//                new AuthorizationFilterException());
//        request.getRequestDispatcher("/error").forward(request, response);

    }
}

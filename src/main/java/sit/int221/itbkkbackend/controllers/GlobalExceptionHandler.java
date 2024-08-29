package sit.int221.itbkkbackend.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.*;

import javax.naming.AuthenticationException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DeleteItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeleteItemNotFoundException(Exception e, HttpServletRequest request){
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        ErrorResponse error = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                "The task does not exist",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicateStatusNameException.class)
    public ResponseEntity<ErrorResponse> handleUniqueConstraintException(DuplicateStatusNameException e, HttpServletRequest request){
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        ErrorResponse error = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                errorDetails.getTitle(),
                String.format("the status name %s is already in use.",e.getReason()),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException exception, HttpServletRequest request){
        Set<ConstraintViolation<?>> errors =  exception.getConstraintViolations();
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        String RootEntityErrorName = exception.getConstraintViolations().iterator().next().getLeafBean().getClass().getSimpleName();
        ErrorResponse resBody = new ErrorResponse(
                errorDetails.getStatus(),
                String.format("Validation error. Check 'errors' field for details. %sForCreateOrUpdate" , RootEntityErrorName),
                request.getRequestURI());
        for (ConstraintViolation<?> error : errors){
            String field = null;
            for (Path.Node node : error.getPropertyPath()) {
                field = node.getName();
            }
            resBody.addValidationError(field,error.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleTaskValidationException(CustomConstraintViolationException exception, HttpServletRequest request){
        Set<ConstraintViolation<?>> errors =  exception.getConstraintViolations();
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        String RootEntityErrorName = exception.getConstraintViolations().iterator().next().getLeafBean().getClass().getSimpleName();
        ErrorResponse resBody = new ErrorResponse(errorDetails.getStatus(),
                String.format("Validation error. Check 'errors' field for details. %sForCreateOrUpdate" ,RootEntityErrorName),
                request.getRequestURI());
        for (ConstraintViolation<?> error : errors){
            String field = null;
            for (Path.Node node : error.getPropertyPath()) {
                field = node.getName();
            }
            resBody.addValidationError(field,error.getMessage());
        }
        if (!exception.getAdditionalErrorFields().isEmpty()){
            for (Map.Entry<String,String> entry :
                 exception.getAdditionalErrorFields().entrySet()) {
                resBody.addValidationError(entry.getKey(),entry.getValue());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resBody);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthorizationFilterException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorization(Exception e, HttpServletRequest request){
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        ErrorResponse error = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                "Invalid Token",
                request.getRequestURI()
        );
        return new ResponseEntity<>(error,HttpStatus.UNAUTHORIZED);
    }


}

package sit.int221.itbkkbackend.controllers;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.itbkkbackend.exceptions.DuplicateStatusNameException;
import sit.int221.itbkkbackend.exceptions.ErrorResponse;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(DeleteItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeleteItemNotFoundException(Exception e, HttpServletRequest request){
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        ErrorResponse error = new ErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                "NOT FOUND",
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


}

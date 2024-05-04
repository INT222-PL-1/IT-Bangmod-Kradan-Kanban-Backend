package sit.int221.itbkkbackend.controllers;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sit.int221.itbkkbackend.exceptions.DeleteErrorResponse;
import sit.int221.itbkkbackend.exceptions.DeleteItemNotFoundException;

import java.sql.Timestamp;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DeleteItemNotFoundException.class)
    public ResponseEntity<DeleteErrorResponse> handleDeleteItemNotFoundException(Exception e, HttpServletRequest request){
        ProblemDetail errorDetails = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        errorDetails.setProperty("asfsasafsafa",request.getRequestURI());
        DeleteErrorResponse error = new DeleteErrorResponse(
                new Timestamp(System.currentTimeMillis()),
                errorDetails.getStatus(),
                "NOT FOUND",
                request.getRequestURI()
//                errorDetails.getInstance()
                );
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

}

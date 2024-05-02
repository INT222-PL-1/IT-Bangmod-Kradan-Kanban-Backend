package sit.int221.itbkkbackend.controllers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Object> XD (){
//        return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }
}

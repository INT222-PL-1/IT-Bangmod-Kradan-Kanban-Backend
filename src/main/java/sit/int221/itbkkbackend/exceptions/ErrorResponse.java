package sit.int221.itbkkbackend.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sit.int221.itbkkbackend.auth.utils.enums.ErrorType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private ErrorType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ")
    private Timestamp timestamp;
    private Integer status;
    private String error;
    private String message;
    private String instance;
    private String path;
    private List<ValidationError> errors;

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }


    public ErrorResponse(Timestamp timestamp, Integer status, String message, String instance) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.instance = instance;
    }
    public ErrorResponse(ErrorType errorType,Timestamp timestamp, Integer status, String message, String instance) {
        this.type = errorType;
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.instance = instance;
    }

    public ErrorResponse(Timestamp timestamp, Integer status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(Integer status, String message , String instance){
        this.status = status;
        this.message = message;
        this.instance = instance;
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}

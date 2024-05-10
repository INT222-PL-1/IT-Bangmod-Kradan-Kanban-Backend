package sit.int221.itbkkbackend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Timestamp timestamp;
    private Integer status;
    private String error;
    private String message;
    private String instance;
    private String path;

    public ErrorResponse(Timestamp timestamp, Integer status, String message, String instance) {
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
}

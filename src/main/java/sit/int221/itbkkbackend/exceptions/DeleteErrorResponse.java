package sit.int221.itbkkbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class DeleteErrorResponse {
    private Timestamp timestamp;
    private Integer status;
    private String message;
    private String instance;
}

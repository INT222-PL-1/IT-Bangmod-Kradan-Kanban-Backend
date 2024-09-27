package sit.int221.itbkkbackend.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    TOKEN_EXPIRED("JWT Token expired"),
    TOKEN_NOT_WELL_FORMED("JWT Token is not well formed"),
    TOKEN_TAMPERED("JWT token has been tampered with"),
    TOKEN_NOT_BEGIN_WITH_BEARER("JWT Token does not begin with Bearer String"),
    UNAUTHORIZED_PRIVATE_ACCESS("Access Denied. You do not have permission to view or access this private board."),
    UNAUTHORIZED_UPDATE("Unauthorized update. You do not have permissions to perform this action."),
    AUTHENTICATION_FAILED("Authentication Failed, Please Try Again"),
    AUTHORIZATION_FAILED("Authorized Failed, Please Try Again");

    private final String message;

}

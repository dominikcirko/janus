package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends JanusBaseException {
    public ForbiddenException(String message) {
        super(
                message,
                "FORBIDDEN",
                HttpStatus.FORBIDDEN,
                "You don't have permission to access this resource",
                "Contact your administrator for access"
        );
    }
}

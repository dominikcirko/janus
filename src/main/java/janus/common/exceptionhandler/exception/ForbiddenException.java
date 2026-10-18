package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends JanusException {
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

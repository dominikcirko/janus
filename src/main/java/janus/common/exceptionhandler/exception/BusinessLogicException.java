package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public final class BusinessLogicException extends JanusBaseException {
    public BusinessLogicException(String message, String... suggestions) {
        super(
                message,
                "BUSINESS_LOGIC_ERROR",
                HttpStatus.UNPROCESSABLE_CONTENT,
                suggestions
        );
    }
}

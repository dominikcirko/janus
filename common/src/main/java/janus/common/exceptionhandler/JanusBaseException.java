package janus.common.exceptionhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JanusBaseException extends RuntimeException{

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String[] suggestions;

    public JanusBaseException(String message, String errorCode,
                              HttpStatus httpStatus, String... suggestions) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.suggestions = suggestions;
    }

    public JanusBaseException(String message, Throwable cause, String errorCode,
                              HttpStatus httpStatus, String... suggestions) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.suggestions = suggestions;
    }
}

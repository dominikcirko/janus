package janus.common.exceptionhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JanusException extends RuntimeException{

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String[] suggestions;

    public JanusException(String message, String errorCode,
                          HttpStatus httpStatus, String... suggestions) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.suggestions = suggestions;
    }
}

package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends JanusBaseException {
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                "RESOURCE_ALREADY_EXISTS",
                HttpStatus.CONFLICT,
                "Use a different " + fieldName,
                "Update the existing resource instead of creating new one"
        );
    }
}

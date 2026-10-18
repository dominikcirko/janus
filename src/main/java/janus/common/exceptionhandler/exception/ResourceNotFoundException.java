package janus.common.exceptionhandler.exception;

import janus.common.exceptionhandler.JanusException;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends JanusException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND,
                "Check if the " + fieldName + " is correct",
                "Verify the resource hasn't been deleted"
        );
    }
}

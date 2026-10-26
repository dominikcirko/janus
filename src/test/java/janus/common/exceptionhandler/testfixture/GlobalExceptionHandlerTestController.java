package janus.common.exceptionhandler.testfixture;

import jakarta.validation.Valid;
import janus.common.exceptionhandler.JanusBaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides endpoints that exercise each branch of the global exception handler
 * through Spring MVC's real exception resolution.
 */
@RestController
public class GlobalExceptionHandlerTestController {

    public static final String JANUS_EXCEPTION_ENDPOINT = "/test-errors/janus-exception";
    public static final String REQUEST_VALIDATION_ENDPOINT = "/test-errors/request-validation";
    public static final String TYPE_MISMATCH_ENDPOINT = "/test-errors/type-mismatch";
    public static final String NULL_POINTER_EXCEPTION_ENDPOINT = "/test-errors/null-pointer-exception";
    public static final String UNEXPECTED_EXCEPTION_ENDPOINT = "/test-errors/unexpected-exception";

    @GetMapping(JANUS_EXCEPTION_ENDPOINT)
    public void throwJanusException() {
        throw new JanusBaseException(
                "Test conflict",
                "TEST_CONFLICT",
                HttpStatus.CONFLICT,
                "Resolve the conflict"
        );
    }

    @PostMapping(REQUEST_VALIDATION_ENDPOINT)
    public GlobalExceptionHandlerTestRequest validateRequest(
            @Valid @RequestBody GlobalExceptionHandlerTestRequest request) {
        return request;
    }

    @GetMapping(TYPE_MISMATCH_ENDPOINT)
    public Integer requireInteger(@RequestParam("value") Integer value) {
        return value;
    }

    @GetMapping(NULL_POINTER_EXCEPTION_ENDPOINT)
    public void throwNullPointerException() {
        throw new NullPointerException("Sensitive internal message");
    }

    @GetMapping(UNEXPECTED_EXCEPTION_ENDPOINT)
    public void throwUnexpectedException() {
        throw new IllegalStateException("Sensitive internal message");
    }
}

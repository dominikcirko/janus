package janus.common.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import janus.common.exceptionhandler.dto.ApiResponse;
import janus.common.exceptionhandler.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug:false}")
    private boolean debugMode;

    @ExceptionHandler(JanusBaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleJanusException(
            JanusBaseException ex,
            HttpServletRequest request){
        log.error("Janus exception occurred: {}", ex.getMessage(), ex);

        var errorDetails = ErrorDetails.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .statusCode(ex.getHttpStatus().value())
                .suggestions(Arrays.asList(ex.getSuggestions()))
                .debugMessage(debugMode ? ex.toString() : null)
                .build();

        ApiResponse<Void> response = ApiResponse.error(errorDetails, request.getRequestURI());

        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.error("Validation error occurred: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Invalid request parameters")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .details(errors)
                .suggestions(Arrays.asList(
                        "Check all required fields",
                        "Ensure data types are correct",
                        "Refer to API documentation"
                ))
                .debugMessage(debugMode ? ex.toString() : null)
                .build();

        ApiResponse<Void> response = ApiResponse.error(errorDetails, request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // handle Type Mismatch Errors (e.g., String instead of Integer)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.error("Type mismatch error: {}", ex.getMessage());

        assert ex.getRequiredType() != null;
        String message = String.format(
                "Parameter '%s' should be of type '%s'",
                ex.getName(),
                ex.getRequiredType().getSimpleName()
        );

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("TYPE_MISMATCH")
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .suggestions(Arrays.asList(
                        "Check the parameter type in your request",
                        "Ensure you're sending the correct data type"
                ))
                .debugMessage(debugMode ? ex.toString() : null)
                .build();

        ApiResponse<Void> response = ApiResponse.error(errorDetails, request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(
            NullPointerException ex,
            HttpServletRequest request) {

        log.error("Null pointer exception occurred: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("NULL_POINTER_ERROR")
                .message("An unexpected error occurred while processing your request")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .suggestions(Arrays.asList(
                        "Please try again",
                        "If the problem persists, contact support"
                ))
                .debugMessage(debugMode ? ex.toString() : null)
                .build();

        ApiResponse<Void> response = ApiResponse.error(errorDetails, request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = ErrorDetails.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Our team has been notified.")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .suggestions(Arrays.asList(
                        "Please try again later",
                        "Contact support if the issue persists"
                ))
                .debugMessage(debugMode ? ex.toString() : null)
                .build();

        ApiResponse<Void> response = ApiResponse.error(errorDetails, request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

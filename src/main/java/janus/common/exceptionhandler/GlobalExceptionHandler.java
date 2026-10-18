package janus.common.exceptionhandler;

import jakarta.servlet.http.HttpServletRequest;
import janus.common.exceptionhandler.dto.ApiResponse;
import janus.common.exceptionhandler.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.debug:false}")
    private boolean debugMode;

    @ExceptionHandler(JanusException.class)
    public ResponseEntity<ApiResponse<Void>> handleJanusException(
            JanusException ex,
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

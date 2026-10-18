package janus.common.exceptionhandler.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorDetails(
        String errorCode,
        String message,
        int statusCode,
        List<String> details,
        List<String> suggestions, // what user should do next
        String debugMessage
) {
}

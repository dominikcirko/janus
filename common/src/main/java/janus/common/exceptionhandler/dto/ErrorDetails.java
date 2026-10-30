package janus.common.exceptionhandler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorDetails(
        String errorCode,
        String message,
        int statusCode,
        List<String> details,
        List<String> suggestions, // what user should do next
        String debugMessage
) {
}

package janus.auth.oauth2.client.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientRegistrationErrorResponse(
        String error,

        @JsonProperty("error_description")
        String errorDescription
) {
}

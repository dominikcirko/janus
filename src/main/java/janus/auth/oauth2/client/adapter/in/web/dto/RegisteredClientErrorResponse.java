package janus.auth.oauth2.client.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisteredClientErrorResponse(
        String error,

        @JsonProperty("error_description")
        String errorDescription
) {
}

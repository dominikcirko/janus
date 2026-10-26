package janus.auth.oauth2.client.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Registration error body containing the RFC error code and optional description.
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7591#section-3.2.2">RFC 7591, Section 3.2.2</a>
 */
public record RegisteredClientErrorResponse(
        String error,

        @JsonProperty("error_description")
        String errorDescription
) {
}

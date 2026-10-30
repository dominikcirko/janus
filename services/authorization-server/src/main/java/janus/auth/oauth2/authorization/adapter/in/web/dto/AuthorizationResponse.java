package janus.auth.oauth2.authorization.adapter.in.web.dto;

import java.util.Objects;

public record AuthorizationResponse(String code, String state) {

    public AuthorizationResponse {
        Objects.requireNonNull(code, "Authorization code must not be null");
    }
}

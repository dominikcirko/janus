package janus.identity.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UserUpdateRequest(
        String displayName,
        String givenName,
        String familyName,
        String pictureUri,
        @NotNull Map<String, Object> userMetadata
) {
}

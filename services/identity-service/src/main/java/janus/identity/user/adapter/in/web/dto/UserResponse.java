package janus.identity.user.adapter.in.web.dto;

import janus.identity.user.domain.type.UserStatus;

import java.util.Map;
import java.util.UUID;

public record UserResponse(
        UUID id,
        UserStatus status,
        String email,
        boolean emailVerified,
        String displayName,
        String givenName,
        String familyName,
        String pictureUri,
        Map<String, Object> userMetadata,
        Map<String, Object> systemMetadata
) {
}

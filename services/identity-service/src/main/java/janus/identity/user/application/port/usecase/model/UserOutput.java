package janus.identity.user.application.port.usecase.model;

import janus.identity.user.domain.type.UserStatus;

import java.util.Map;
import java.util.UUID;

public record UserOutput(
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

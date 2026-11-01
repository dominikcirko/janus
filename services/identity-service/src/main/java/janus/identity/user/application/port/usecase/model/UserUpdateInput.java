package janus.identity.user.application.port.usecase.model;

import java.util.Map;

public record UserUpdateInput(
        String displayName,
        String givenName,
        String familyName,
        String pictureUri,
        Map<String, Object> userMetadata
) {
}

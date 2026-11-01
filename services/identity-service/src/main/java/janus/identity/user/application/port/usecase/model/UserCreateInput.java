package janus.identity.user.application.port.usecase.model;

import java.util.Map;

public record UserCreateInput(
        String email,
        String password,
        String displayName,
        String givenName,
        String familyName,
        String pictureUri,
        Map<String, Object> userMetadata
) {
}

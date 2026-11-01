package janus.identity.user.application.port.usecase.model;

import janus.identity.user.domain.type.UserStatus;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class User {

    UUID id;
    UserStatus status;
    String email;
    boolean emailVerified;
    String displayName;
    String givenName;
    String familyName;
    String pictureUri;
    Map<String, Object> userMetadata;
    Map<String, Object> systemMetadata;

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}

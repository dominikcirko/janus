package janus.identity.user.application.port.usecase.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PasswordCredential {

    UUID userId;
    String passwordHash;
    Instant passwordChangedAt;
    boolean mustChangePassword;
}

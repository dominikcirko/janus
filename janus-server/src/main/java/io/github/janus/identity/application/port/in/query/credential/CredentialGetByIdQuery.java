package io.github.janus.identity.application.port.in.query.credential;

import java.util.UUID;

public record CredentialGetByIdQuery(UUID id, UUID userId) {
}

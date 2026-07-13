package io.github.janus.identity.credential.application.port.in.query;

import java.util.UUID;

public record CredentialGetByIdQuery(UUID id, UUID userId) {
}

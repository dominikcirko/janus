package io.github.janus.identity.application.port.in.query;

import java.util.UUID;

public record UserGetByIdQuery(UUID userId,
                               UUID tenantId) {}

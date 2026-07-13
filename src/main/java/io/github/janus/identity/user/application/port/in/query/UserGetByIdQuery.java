package io.github.janus.identity.user.application.port.in.query;

import java.util.UUID;

public record UserGetByIdQuery(UUID id,
                               UUID tenantId) {}

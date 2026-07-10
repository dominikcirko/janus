package io.github.janus.identity.application.port.in.command.user;

import java.util.UUID;

public record UserDeleteCommand(UUID userId,
                                UUID tenantId,
                                UUID actorId ) {}

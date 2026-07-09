package io.github.janus.identity.application.port.in.command;

import java.util.UUID;

public record UserDeleteCommand(UUID userId,
                                UUID tenantId,
                                UUID actorId ) {}

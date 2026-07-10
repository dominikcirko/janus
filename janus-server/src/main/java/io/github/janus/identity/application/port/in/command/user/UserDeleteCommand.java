package io.github.janus.identity.application.port.in.command.user;

import java.util.UUID;

//todo: add actorId -> who deleted this user
public record UserDeleteCommand(UUID userId,
                                UUID tenantId) {}

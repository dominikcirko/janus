package io.github.janus.identity.application.port.in.command;

import java.util.UUID;

public record UserGetCommand(UUID userId,
                             UUID tenantId) {}

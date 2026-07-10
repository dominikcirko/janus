package io.github.janus.identity.application.port.in.command.credential;

import java.util.UUID;

public record ResetPasswordCommand(UUID userId,
                                   UUID tenantId,
                                   String newPassword) {}

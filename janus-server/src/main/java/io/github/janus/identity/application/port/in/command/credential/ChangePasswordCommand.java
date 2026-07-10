package io.github.janus.identity.application.port.in.command.credential;

import java.util.UUID;

public record ChangePasswordCommand(UUID userId,
                                    UUID tenantId,
                                    String oldPassword,
                                    String newPassword) {}

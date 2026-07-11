package io.github.janus.identity.role.application.port.in.command;

import java.util.List;
import java.util.UUID;

public record AssignPermissionsCommand(UUID roleId,
                                       List<UUID> permissionIds) {}

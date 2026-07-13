package io.github.janus.identity.user.application.port.in.command;

import java.util.Set;
import java.util.UUID;

public record UserUpdateCommand(UUID id,
                                String username,
                                String familyName,
                                String givenName,
                                String email,
                                Set<UUID> roleIds,
                                boolean verified) {}

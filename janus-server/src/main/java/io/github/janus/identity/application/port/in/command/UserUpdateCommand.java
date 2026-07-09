package io.github.janus.identity.application.port.in.command;

import java.util.Set;
import java.util.UUID;

public record UserUpdateCommand(String username,
                                String familyName,
                                String givenName,
                                String email,
                                Set<UUID> roleIds,
                                boolean verified) {}

package io.github.janus.identity.application.port.in.command.user;

import io.github.janus.identity.domain.type.CredentialType;

import java.util.Set;
import java.util.UUID;

public record UserCreateCommand(String username,
                                String familyName,
                                String givenName,
                                String email,
                                Set<UUID> roleIds,
                                UUID tenantId,
                                String credentialValue,
                                CredentialType credentialType,
                                boolean verified) {}

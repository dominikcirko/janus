package io.github.janus.identity.user.application.port.in.command;

import io.github.janus.identity.credential.domain.type.CredentialType;

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

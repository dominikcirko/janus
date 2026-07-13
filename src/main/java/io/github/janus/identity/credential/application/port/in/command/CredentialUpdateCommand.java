package io.github.janus.identity.credential.application.port.in.command;

import io.github.janus.identity.credential.domain.type.CredentialType;

import java.util.UUID;

public record CredentialUpdateCommand(UUID id,
                                      CredentialType type,
                                      String value,
                                      UUID userId) {}

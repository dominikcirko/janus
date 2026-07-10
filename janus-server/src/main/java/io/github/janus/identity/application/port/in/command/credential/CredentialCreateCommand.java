package io.github.janus.identity.application.port.in.command.credential;

import io.github.janus.identity.domain.type.CredentialType;

import java.util.UUID;

public record CredentialCreateCommand(CredentialType type,
                                      String value,
                                      UUID userId) {}
package io.github.janus.identity.credential.application.port.in.command;

import io.github.janus.identity.credential.domain.type.CredentialType;

import java.util.UUID;

public record CredentialCreateCommand(CredentialType type,
                                      String value,
                                      UUID userId) {}
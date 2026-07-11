package io.github.janus.identity.user.application.port.in.query;

import java.util.UUID;

public record UserGetByEmailQuery(String email,
                                  UUID tenantId) {}

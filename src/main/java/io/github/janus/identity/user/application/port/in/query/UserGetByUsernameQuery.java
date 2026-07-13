package io.github.janus.identity.user.application.port.in.query;

import java.util.UUID;

public record UserGetByUsernameQuery(String username,
                                     UUID tenantId) {}

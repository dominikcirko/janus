package io.github.janus.identity.application.port.in.query;

import java.util.UUID;

public record UserGetByUsernameQuery(String username,
                                     UUID tenantId) {
}

package io.github.janus.identity.application.port.in.query.user;

import java.util.UUID;

public record UserGetByEmailQuery(String email,
                                  UUID tenantId) {
}

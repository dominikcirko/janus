package io.github.janus.identity;

import java.util.Set;
import java.util.UUID;

public record UserRequest(
        String username,
        String familyName,
        String givenName,
        String email,
        Set<UUID> roleIds,
        UUID tenantId,
        String password) {
}

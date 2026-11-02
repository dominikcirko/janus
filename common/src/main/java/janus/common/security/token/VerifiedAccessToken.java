package janus.common.security.token;

import java.util.Set;
import java.util.UUID;

public record VerifiedAccessToken(
        String issuer,
        String subject,
        UUID tenantId,
        String clientId,
        Set<String> audiences,
        Set<String> scopes
) {}

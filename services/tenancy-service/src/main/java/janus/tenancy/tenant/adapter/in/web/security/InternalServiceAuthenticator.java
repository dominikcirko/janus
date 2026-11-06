package janus.tenancy.tenant.adapter.in.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class InternalServiceAuthenticator {

    private static final String BEARER_PREFIX = "Bearer ";

    private final byte[] serviceToken;

    public InternalServiceAuthenticator(
            @Value("${janus.security.tenant-issuer-service-token}") String serviceToken) {
        this.serviceToken = serviceToken.strip().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isAuthenticated(String authorization) {
        if (serviceToken.length == 0 || authorization == null
                || !authorization.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return false;
        }

        byte[] presentedToken = authorization.substring(BEARER_PREFIX.length())
                .stripLeading()
                .getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(serviceToken, presentedToken);
    }
}

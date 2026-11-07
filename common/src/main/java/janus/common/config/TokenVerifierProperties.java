package janus.common.config;

import janus.common.exceptionhandler.exception.InvalidConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.List;

@ConfigurationProperties(prefix = "janus.security")
public record TokenVerifierProperties(
        String audience,
        URI tenantIssuerServiceUrl,
        String tenantIssuerServiceToken,
        List<String> protectedPaths
) {

    public TokenVerifierProperties {
        if (audience == null || audience.isBlank()) {
            throw new InvalidConfigurationException("janus.security.audience must be configured");
        }
        if (tenantIssuerServiceUrl == null || tenantIssuerServiceUrl.getHost() == null
                || !("http".equalsIgnoreCase(tenantIssuerServiceUrl.getScheme())
                || "https".equalsIgnoreCase(tenantIssuerServiceUrl.getScheme()))) {
            throw new InvalidConfigurationException(
                    "janus.security.tenant-issuer-service-url must be an absolute HTTP or HTTPS URL"
            );
        }
        if (tenantIssuerServiceToken == null || tenantIssuerServiceToken.isBlank()) {
            throw new InvalidConfigurationException(
                    "janus.security.tenant-issuer-service-token must be configured"
            );
        }
        tenantIssuerServiceToken = tenantIssuerServiceToken.strip();
        if (protectedPaths == null || protectedPaths.isEmpty()) {
            throw new InvalidConfigurationException("janus.security.protected-paths must be configured");
        }
        for (String path : protectedPaths) {
            if (path == null || path.isBlank()) {
                throw new InvalidConfigurationException("Protected paths must not be blank");
            }
        }
        protectedPaths = List.copyOf(protectedPaths);
    }
}

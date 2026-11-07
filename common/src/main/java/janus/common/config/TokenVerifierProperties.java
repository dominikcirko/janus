package janus.common.config;

import janus.common.exceptionhandler.exception.InvalidConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "janus.security")
public record TokenVerifierProperties(
        String audience,
        URI tenantIssuerServiceUrl,
        String tenantIssuerServiceToken
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
    }
}

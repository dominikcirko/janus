package janus.common.config;

import janus.common.exceptionhandler.exception.InvalidConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "janus.security")
public record TokenVerifierProperties(String audience) {

    public TokenVerifierProperties {
        if (audience == null || audience.isBlank()) {
            throw new InvalidConfigurationException("janus.security.audience must be configured");
        }
    }
}

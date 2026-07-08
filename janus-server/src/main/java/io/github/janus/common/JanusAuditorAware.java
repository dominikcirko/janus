package io.github.janus.common;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class JanusAuditorAware implements AuditorAware<AuditContext> {

    @Override
    public Optional<AuditContext> getCurrentAuditor() {
        return Optional.empty();
    }
}

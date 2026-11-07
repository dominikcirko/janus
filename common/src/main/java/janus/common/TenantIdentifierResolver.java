package janus.common;

import janus.common.exceptionhandler.exception.InvalidConfigurationException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import java.util.NoSuchElementException;
import java.util.UUID;

public final class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID> {

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        try {
            UUID tenantId = TenantContext.currentTenantId();
            if (tenantId == null) {
                throw new InvalidConfigurationException("Tenant context must contain a tenant ID");
            }
            return tenantId;
        } catch (NoSuchElementException ex) {
            throw new InvalidConfigurationException(
                    "Tenant context must be bound before opening a Hibernate session"
            );
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}

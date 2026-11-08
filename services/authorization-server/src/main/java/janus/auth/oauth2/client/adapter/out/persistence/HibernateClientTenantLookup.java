package janus.auth.oauth2.client.adapter.out.persistence;

import jakarta.persistence.EntityManagerFactory;
import janus.auth.oauth2.client.application.port.out.ClientTenantLookup;
import janus.common.TenantIdentifierResolver;
import lombok.RequiredArgsConstructor;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HibernateClientTenantLookup implements ClientTenantLookup, CurrentTenantIdentifierResolver<UUID> {

    private final ScopedValue<UUID> rootTenant = ScopedValue.newInstance();

    private final ObjectProvider<EntityManagerFactory> entityManagerFactory;
    private final TenantIdentifierResolver tenantIdentifierResolver;

    /**
     * Before an access token exists, we need another way to find the tenant.
     * Normal Hibernate sessions require tenant context, so we use a root session
     * to find the registered client's tenant and establish that context first.
     */
    @Override
    public Optional<UUID> findTenantIdByClientId(String clientId) {
        return ScopedValue.where(rootTenant, UUID.randomUUID()).call(() -> {
            try (var session = entityManagerFactory.getObject().unwrap(SessionFactory.class).openSession()) {
                session.setDefaultReadOnly(true);
                session.setHibernateFlushMode(FlushMode.MANUAL);

                return session.createSelectionQuery(
                                """
                                select client.tenantId
                                from RegisteredClientEntity client
                                where client.clientId = :clientId
                                """,
                                UUID.class
                        )
                        .setParameter("clientId", clientId)
                        .uniqueResultOptional();
            }
        });
    }

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        if (rootTenant.isBound()) {
            return rootTenant.get();
        }
        return tenantIdentifierResolver.resolveCurrentTenantIdentifier();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return tenantIdentifierResolver.validateExistingCurrentSessions();
    }

    @Override
    public boolean isRoot(UUID tenantId) {
        return rootTenant.isBound() && rootTenant.get().equals(tenantId);
    }
}

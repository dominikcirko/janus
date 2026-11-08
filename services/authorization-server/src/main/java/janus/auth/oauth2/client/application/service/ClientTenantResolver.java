package janus.auth.oauth2.client.application.service;

import janus.auth.oauth2.client.application.port.out.ClientTenantLookup;
import janus.common.TenantContext;
import janus.common.exceptionhandler.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientTenantResolver {

    private final ClientTenantLookup clientTenantLookup;

    public UUID resolveClientTenant(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new InvalidRequestException("client_id must not be null or blank");
        }

        UUID tenantId = clientTenantLookup.findTenantIdByClientId(clientId)
                .orElseThrow(() -> new InvalidRequestException("Invalid client_id"));

        if (TenantContext.isBound() && !tenantId.equals(TenantContext.currentTenantId())) {
            throw new InvalidRequestException("Client does not belong to the current tenant");
        }
        return tenantId;
    }

    public UUID resolveGrantTenant(String clientId, String storedGrantClientId, UUID storedGrantTenantId) {
        if (storedGrantClientId == null || storedGrantClientId.isBlank() || storedGrantTenantId == null) {
            throw new InvalidRequestException("Stored grant must identify its client and tenant");
        }
        if (!storedGrantClientId.equals(clientId)) {
            throw new InvalidRequestException("Grant does not belong to the client");
        }

        UUID tenantId = resolveClientTenant(clientId);
        if (!tenantId.equals(storedGrantTenantId)) {
            throw new InvalidRequestException("Grant tenant does not match the registered client");
        }
        return tenantId;
    }
}

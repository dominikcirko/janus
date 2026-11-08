package janus.auth.oauth2.client.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface ClientTenantLookup {

    Optional<UUID> findTenantIdByClientId(String clientId);
}

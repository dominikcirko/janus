package janus.common.security.token;

import janus.common.exceptionhandler.exception.InvalidServiceResponseException;
import janus.common.security.token.exception.InvalidAccessTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

@RequiredArgsConstructor
public final class TenantIssuerClient {

    private final RestClient restClient;

    public String getIssuer(UUID tenantId) {
        JsonNode response;
        try {
            response = restClient.get()
                    .uri("/internal/tenants/{tenantId}/issuer", tenantId)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new InvalidAccessTokenException("Unknown token tenant", ex);
        }

        if (response == null || !response.isObject()) {
            throw new InvalidServiceResponseException("Tenant issuer response must be a JSON object");
        }

        JsonNode issuer = response.get("issuer");
        if (issuer == null || !issuer.isString() || issuer.stringValue().isBlank()) {
            throw new InvalidServiceResponseException("Tenant issuer response must contain a nonblank issuer");
        }

        return issuer.stringValue();
    }
}

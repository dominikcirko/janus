package janus.tenancy.tenant.adapter.in.web;

import janus.tenancy.tenant.adapter.in.web.dto.TenantIssuerResponse;
import janus.tenancy.tenant.adapter.in.web.security.InternalServiceAuthenticator;
import janus.tenancy.tenant.application.port.usecase.TenantUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/tenants")
@RequiredArgsConstructor
public class InternalTenantIssuerController {

    private final TenantUseCase tenantUseCase;
    private final InternalServiceAuthenticator authenticator;

    @GetMapping(value = "/{tenantId}/issuer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TenantIssuerResponse> getIssuer(
            @PathVariable UUID tenantId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (!authenticator.isAuthenticated(authorization)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                    .build();
        }

        var tenant = tenantUseCase.getTenant(tenantId);
        return ResponseEntity.ok(new TenantIssuerResponse(tenant.issuer()));
    }
}

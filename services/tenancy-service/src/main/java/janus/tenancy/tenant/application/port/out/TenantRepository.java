package janus.tenancy.tenant.application.port.out;

import janus.tenancy.tenant.application.port.usecase.model.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {

    Tenant save(Tenant tenant);
    Optional<Tenant> findById(UUID id);
    List<Tenant> findAllByOrganizationId(UUID organizationId);
    boolean existsByOrganizationIdAndSlug(UUID organizationId, String slug);
    boolean existsByIssuer(String issuer);
    void deleteById(UUID id);
}

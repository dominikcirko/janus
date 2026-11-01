package janus.tenancy.tenant.application.port.out;

import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantMembershipRepository {

    TenantMembership save(TenantMembership membership);
    Optional<TenantMembership> findById(UUID id);
    List<TenantMembership> findAllByTenantId(UUID tenantId);
    boolean existsByTenantIdAndUserId(UUID tenantId, UUID userId);
    void deleteById(UUID id);
}

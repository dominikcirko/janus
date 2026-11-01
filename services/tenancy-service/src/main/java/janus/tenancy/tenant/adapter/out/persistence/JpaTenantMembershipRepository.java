package janus.tenancy.tenant.adapter.out.persistence;

import janus.tenancy.tenant.adapter.out.persistence.entity.TenantMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaTenantMembershipRepository extends JpaRepository<TenantMembershipEntity, UUID> {

    List<TenantMembershipEntity> findAllByTenantId(UUID tenantId);
    boolean existsByTenantIdAndUserId(UUID tenantId, UUID userId);
}

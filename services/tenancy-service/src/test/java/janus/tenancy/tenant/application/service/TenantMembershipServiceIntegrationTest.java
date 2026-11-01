package janus.tenancy.tenant.application.service;

import jakarta.persistence.EntityManager;
import janus.TestcontainersConfiguration;
import janus.common.config.JpaAuditingConfig;
import janus.tenancy.organization.adapter.out.persistence.JpaOrganizationRepository;
import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.tenant.adapter.out.persistence.JpaTenantMembershipRepository;
import janus.tenancy.tenant.adapter.out.persistence.JpaTenantRepository;
import janus.tenancy.tenant.adapter.out.persistence.TenantMembershipRepositoryAdapter;
import janus.tenancy.tenant.adapter.out.persistence.TenantRepositoryAdapter;
import janus.tenancy.tenant.adapter.out.persistence.entity.TenantEntity;
import janus.tenancy.tenant.adapter.out.persistence.mapper.TenantMembershipPersistenceMapperImpl;
import janus.tenancy.tenant.adapter.out.persistence.mapper.TenantPersistenceMapperImpl;
import janus.tenancy.tenant.application.mapper.TenantMembershipMapperImpl;
import janus.tenancy.tenant.domain.type.TenantEnvironment;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.domain.type.TenantStatus;
import janus.tenancy.tenant.testdata.TenantMembershipTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TenantMembershipService.class,
        TenantMembershipRepositoryAdapter.class,
        TenantMembershipPersistenceMapperImpl.class,
        TenantMembershipMapperImpl.class,
        TenantRepositoryAdapter.class,
        TenantPersistenceMapperImpl.class,
        JpaAuditingConfig.class,
        TestcontainersConfiguration.class
})
public class TenantMembershipServiceIntegrationTest {

    @Autowired
    private TenantMembershipService tenantMembershipService;

    @Autowired
    private JpaTenantMembershipRepository tenantMembershipRepository;

    @Autowired
    private JpaTenantRepository tenantRepository;

    @Autowired
    private JpaOrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testWhenTenantMembershipInputValid_thenPersistMembership() {
        var organization = OrganizationEntity.builder()
                .name("Membership Test Organization")
                .slug("membership-test-organization")
                .status(OrganizationStatus.ACTIVE)
                .build();
        var savedOrganization = organizationRepository.saveAndFlush(organization);
        var tenant = TenantEntity.builder()
                .organization(savedOrganization)
                .name("Membership Test Tenant")
                .slug("membership-test-tenant")
                .environment(TenantEnvironment.DEVELOPMENT)
                .status(TenantStatus.ACTIVE)
                .issuer("https://janus.example.com/membership-test-tenant")
                .build();
        var savedTenant = tenantRepository.saveAndFlush(tenant);
        var input = TenantMembershipTestDataFactory.createValidInput();

        var result = tenantMembershipService.createTenantMembership(savedTenant.getId(), input);
        entityManager.flush();
        entityManager.clear();

        var persistedMembership = tenantMembershipRepository.findById(result.id()).orElseThrow();

        assertEquals(savedTenant.getId(), persistedMembership.getTenant().getId());
        assertEquals(input.userId(), persistedMembership.getUserId());
        assertEquals(input.role(), persistedMembership.getRole());
        assertEquals(TenantMembershipStatus.ACTIVE, persistedMembership.getStatus());
        assertEquals(input.metadata(), persistedMembership.getMetadata());
    }
}

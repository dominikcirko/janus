package janus.tenancy.tenant.application.service;

import jakarta.persistence.EntityManager;
import janus.TestcontainersConfiguration;
import janus.common.config.JpaAuditingConfig;
import janus.tenancy.organization.adapter.out.persistence.JpaOrganizationRepository;
import janus.tenancy.organization.adapter.out.persistence.OrganizationRepositoryAdapter;
import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import janus.tenancy.organization.adapter.out.persistence.mapper.OrganizationPersistenceMapperImpl;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.tenant.adapter.out.persistence.JpaTenantRepository;
import janus.tenancy.tenant.adapter.out.persistence.TenantRepositoryAdapter;
import janus.tenancy.tenant.adapter.out.persistence.mapper.TenantPersistenceMapperImpl;
import janus.tenancy.tenant.application.mapper.TenantMapperImpl;
import janus.tenancy.tenant.domain.type.TenantStatus;
import janus.tenancy.tenant.testdata.TenantTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        TenantService.class,
        TenantRepositoryAdapter.class,
        TenantPersistenceMapperImpl.class,
        TenantMapperImpl.class,
        OrganizationRepositoryAdapter.class,
        OrganizationPersistenceMapperImpl.class,
        JpaAuditingConfig.class,
        TestcontainersConfiguration.class
})
public class TenantServiceIntegrationTest {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private JpaTenantRepository tenantRepository;

    @Autowired
    private JpaOrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testWhenTenantInputValid_thenPersistTenant() {
        var organization = OrganizationEntity.builder()
                .name("Tenant Test Organization")
                .slug("tenant-test-organization")
                .status(OrganizationStatus.ACTIVE)
                .build();
        var savedOrganization = organizationRepository.saveAndFlush(organization);
        var input = TenantTestDataFactory.createValidInput();

        var result = tenantService.createTenant(savedOrganization.getId(), input);
        entityManager.flush();
        entityManager.clear();

        var persistedTenant = tenantRepository.findById(result.id()).orElseThrow();

        assertEquals(savedOrganization.getId(), persistedTenant.getOrganization().getId());
        assertEquals(input.name(), persistedTenant.getName());
        assertEquals(input.slug(), persistedTenant.getSlug());
        assertEquals(input.environment(), persistedTenant.getEnvironment());
        assertEquals(TenantStatus.ACTIVE, persistedTenant.getStatus());
        assertEquals(input.issuer(), persistedTenant.getIssuer());
    }
}

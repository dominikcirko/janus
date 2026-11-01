package janus.tenancy.organization.application.service;

import jakarta.persistence.EntityManager;
import janus.TestcontainersConfiguration;
import janus.common.config.JpaAuditingConfig;
import janus.tenancy.organization.adapter.out.persistence.JpaOrganizationRepository;
import janus.tenancy.organization.adapter.out.persistence.OrganizationRepositoryAdapter;
import janus.tenancy.organization.adapter.out.persistence.mapper.OrganizationPersistenceMapperImpl;
import janus.tenancy.organization.application.mapper.OrganizationMapperImpl;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        OrganizationService.class,
        OrganizationRepositoryAdapter.class,
        OrganizationPersistenceMapperImpl.class,
        OrganizationMapperImpl.class,
        JpaAuditingConfig.class,
        TestcontainersConfiguration.class
})
public class OrganizationServiceIntegrationTest {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private JpaOrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testWhenOrganizationInputValid_thenPersistOrganization() {
        var input = OrganizationTestDataFactory.createValidInput();

        var result = organizationService.createOrganization(input);
        entityManager.flush();
        entityManager.clear();

        var persistedOrganization = organizationRepository.findById(result.id()).orElseThrow();

        assertEquals(input.name(), persistedOrganization.getName());
        assertEquals(input.slug(), persistedOrganization.getSlug());
        assertEquals(OrganizationStatus.ACTIVE, persistedOrganization.getStatus());
    }
}

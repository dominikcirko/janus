package janus.tenancy.organization.application.service;

import jakarta.persistence.EntityManager;
import janus.TestcontainersConfiguration;
import janus.common.config.JpaAuditingConfig;
import janus.tenancy.organization.adapter.out.persistence.JpaOrganizationMembershipRepository;
import janus.tenancy.organization.adapter.out.persistence.JpaOrganizationRepository;
import janus.tenancy.organization.adapter.out.persistence.OrganizationMembershipRepositoryAdapter;
import janus.tenancy.organization.adapter.out.persistence.OrganizationRepositoryAdapter;
import janus.tenancy.organization.adapter.out.persistence.entity.OrganizationEntity;
import janus.tenancy.organization.adapter.out.persistence.mapper.OrganizationMembershipPersistenceMapperImpl;
import janus.tenancy.organization.adapter.out.persistence.mapper.OrganizationPersistenceMapperImpl;
import janus.tenancy.organization.application.mapper.OrganizationMembershipMapperImpl;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import janus.tenancy.organization.testdata.OrganizationMembershipTestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        OrganizationMembershipService.class,
        OrganizationMembershipRepositoryAdapter.class,
        OrganizationMembershipPersistenceMapperImpl.class,
        OrganizationMembershipMapperImpl.class,
        OrganizationRepositoryAdapter.class,
        OrganizationPersistenceMapperImpl.class,
        JpaAuditingConfig.class,
        TestcontainersConfiguration.class
})
public class OrganizationMembershipServiceIntegrationTest {

    @Autowired
    private OrganizationMembershipService organizationMembershipService;

    @Autowired
    private JpaOrganizationMembershipRepository organizationMembershipRepository;

    @Autowired
    private JpaOrganizationRepository organizationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testWhenOrganizationMembershipInputValid_thenPersistMembership() {
        var organization = OrganizationEntity.builder()
                .name("Membership Test Organization")
                .slug("organization-membership-test")
                .status(OrganizationStatus.ACTIVE)
                .build();
        var savedOrganization = organizationRepository.saveAndFlush(organization);
        var input = OrganizationMembershipTestDataFactory.createValidInput();

        var result = organizationMembershipService.createOrganizationMembership(savedOrganization.getId(), input);
        entityManager.flush();
        entityManager.clear();

        var persistedMembership = organizationMembershipRepository.findById(result.id()).orElseThrow();

        assertEquals(savedOrganization.getId(), persistedMembership.getOrganization().getId());
        assertEquals(input.userId(), persistedMembership.getUserId());
        assertEquals(input.role(), persistedMembership.getRole());
    }
}

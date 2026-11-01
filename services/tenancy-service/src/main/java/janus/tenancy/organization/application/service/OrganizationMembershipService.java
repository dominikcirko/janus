package janus.tenancy.organization.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.mapper.OrganizationMembershipMapper;
import janus.tenancy.organization.application.port.out.OrganizationMembershipRepository;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.OrganizationMembershipUseCase;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipUpdateInput;
import janus.tenancy.organization.domain.type.OrganizationRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationMembershipService implements OrganizationMembershipUseCase {

    private static final String ORGANIZATION = "Organization";
    private static final String ORGANIZATION_MEMBERSHIP = "Organization membership";

    private final OrganizationMembershipRepository repository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMembershipMapper mapper;

    @Override
    @Transactional
    public OrganizationMembershipOutput createOrganizationMembership(UUID organizationId, OrganizationMembershipCreateInput input) {
        validateOrganizationId(organizationId);
        validateCreateInput(input);
        validateOrganizationExists(organizationId);

        if (repository.existsByOrganizationIdAndUserId(organizationId, input.userId())) {
            throw new ResourceAlreadyExistsException(ORGANIZATION_MEMBERSHIP, "userId", input.userId());
        }

        var membership = mapper.toOrganizationMembership(organizationId, input);
        var savedMembership = repository.save(membership);

        return mapper.toOutput(savedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationMembershipOutput getOrganizationMembership(UUID membershipId) {
        var membership = findOrganizationMembership(membershipId);

        return mapper.toOutput(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMembershipOutput> getOrganizationMemberships(UUID organizationId) {
        validateOrganizationId(organizationId);

        return repository.findAllByOrganizationId(organizationId)
                .stream()
                .map(mapper::toOutput)
                .toList();
    }

    @Override
    @Transactional
    public OrganizationMembershipOutput updateOrganizationMembership(UUID membershipId, OrganizationMembershipUpdateInput input) {
        validateUpdateInput(input);

        var membership = findOrganizationMembership(membershipId);
        var updatedMembership = mapper.toUpdatedOrganizationMembership(membership, input);
        var savedMembership = repository.save(updatedMembership);

        return mapper.toOutput(savedMembership);
    }

    @Override
    @Transactional
    public void deleteOrganizationMembership(UUID membershipId) {
        findOrganizationMembership(membershipId);
        repository.deleteById(membershipId);
    }

    private OrganizationMembership findOrganizationMembership(UUID membershipId) {
        return repository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION_MEMBERSHIP, "id", membershipId));
    }

    private void validateOrganizationExists(UUID organizationId) {
        organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION, "id", organizationId));
    }

    private void validateOrganizationId(UUID organizationId) {
        if (organizationId == null) {
            throw new InvalidRequestException("Organization id must not be null");
        }
    }

    private void validateCreateInput(OrganizationMembershipCreateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Organization membership input must not be null");
        }

        if (input.userId() == null) {
            throw new InvalidRequestException("Organization membership user id must not be null");
        }

        validateRole(input.role());
    }

    private void validateUpdateInput(OrganizationMembershipUpdateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Organization membership update input must not be null");
        }

        validateRole(input.role());
    }

    private void validateRole(OrganizationRole role) {
        if (role == null) {
            throw new InvalidRequestException("Organization membership role must not be null");
        }
    }
}

package janus.tenancy.organization.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.mapper.OrganizationMapper;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.OrganizationUseCase;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.application.port.usecase.model.OrganizationInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.domain.type.OrganizationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService implements OrganizationUseCase {

    private static final String ORGANIZATION = "Organization";

    private final OrganizationRepository repository;
    private final OrganizationMapper mapper;

    @Override
    @Transactional
    public OrganizationOutput createOrganization(OrganizationInput input) {
        validateInput(input);

        if (repository.existsBySlug(input.slug())) {
            throw new ResourceAlreadyExistsException(ORGANIZATION, "slug", input.slug());
        }

        var organization = mapper.toOrganization(input);
        var savedOrganization = repository.save(organization);

        return mapper.toOutput(savedOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationOutput getOrganization(UUID organizationId) {
        var organization = findOrganization(organizationId);

        return mapper.toOutput(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationOutput> getOrganizations() {
        return repository.findAll()
                .stream()
                .map(mapper::toOutput)
                .toList();
    }

    @Override
    @Transactional
    public OrganizationOutput updateOrganization(UUID organizationId, OrganizationInput input) {
        validateInput(input);

        var organization = findOrganization(organizationId);
        if (!organization.getSlug().equals(input.slug())
                && repository.existsBySlug(input.slug())) {
            throw new ResourceAlreadyExistsException(ORGANIZATION, "slug", input.slug());
        }

        var updatedOrganization = mapper.updateOrganization(organization, input);
        var savedOrganization = repository.save(updatedOrganization);

        return mapper.toOutput(savedOrganization);
    }

    @Override
    @Transactional
    public OrganizationOutput changeOrganizationStatus(UUID organizationId, OrganizationStatus status) {
        if (status == null) {
            throw new InvalidRequestException("Organization status must not be null");
        }

        var organization = findOrganization(organizationId);
        var updatedOrganization = mapper.changeStatus(organization, status);
        var savedOrganization = repository.save(updatedOrganization);

        return mapper.toOutput(savedOrganization);
    }

    @Override
    @Transactional
    public void deleteOrganization(UUID organizationId) {
        findOrganization(organizationId);
        repository.deleteById(organizationId);
    }

    private Organization findOrganization(UUID organizationId) {
        return repository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(ORGANIZATION, "id",organizationId));
    }

    private void validateInput(OrganizationInput input) {
        if (input == null) {
            throw new InvalidRequestException("Organization input must not be null");
        }

        if (input.name() == null || input.name().isBlank()) {
            throw new InvalidRequestException("Organization name must not be null or blank");
        }

        if (input.slug() == null || input.slug().isBlank()) {
            throw new InvalidRequestException("Organization slug must not be null or blank");
        }
    }
}

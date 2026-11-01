package janus.tenancy.organization.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.mapper.OrganizationMapper;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.organization.application.port.usecase.OrganizationUseCase;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.application.port.usecase.model.OrganizationCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationUpdateInput;
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
    public OrganizationOutput createOrganization(OrganizationCreateInput input) {
        validateCreateInput(input);

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
    public OrganizationOutput updateOrganization(UUID organizationId, OrganizationUpdateInput input) {
        validateUpdateInput(input);

        var organization = findOrganization(organizationId);
        if (!organization.getSlug().equals(input.slug())
                && repository.existsBySlug(input.slug())) {
            throw new ResourceAlreadyExistsException(ORGANIZATION, "slug", input.slug());
        }

        var updatedOrganization = mapper.toUpdatedOrganization(organization, input);
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
        var updatedOrganization = mapper.withUpdatedStatus(organization, status);
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

    private void validateCreateInput(OrganizationCreateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Organization input must not be null");
        }

        validateMutableFields(input.name(), input.slug());
    }

    private void validateUpdateInput(OrganizationUpdateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Organization update input must not be null");
        }

        validateMutableFields(input.name(), input.slug());
    }

    private void validateMutableFields(String name, String slug) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("Organization name must not be null or blank");
        }

        if (slug == null || slug.isBlank()) {
            throw new InvalidRequestException("Organization slug must not be null or blank");
        }
    }
}

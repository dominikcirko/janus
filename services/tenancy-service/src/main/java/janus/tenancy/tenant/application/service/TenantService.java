package janus.tenancy.tenant.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.organization.application.port.out.OrganizationRepository;
import janus.tenancy.tenant.application.mapper.TenantMapper;
import janus.tenancy.tenant.application.port.out.TenantRepository;
import janus.tenancy.tenant.application.port.usecase.TenantUseCase;
import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import janus.tenancy.tenant.application.port.usecase.model.TenantInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantUpdateInput;
import janus.tenancy.tenant.domain.type.TenantEnvironment;
import janus.tenancy.tenant.domain.type.TenantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService implements TenantUseCase {

    private static final String ORGANIZATION = "Organization";
    private static final String TENANT = "Tenant";

    private final TenantRepository repository;
    private final OrganizationRepository organizationRepository;
    private final TenantMapper mapper;

    @Override
    @Transactional
    public TenantOutput createTenant(UUID organizationId, TenantInput input) {
        validateOrganizationId(organizationId);
        validateInput(input);
        validateOrganizationExists(organizationId);

        if (repository.existsByOrganizationIdAndSlug(organizationId, input.slug())) {
            throw new ResourceAlreadyExistsException(TENANT, "slug", input.slug());
        }

        if (repository.existsByIssuer(input.issuer())) {
            throw new ResourceAlreadyExistsException(TENANT, "issuer", input.issuer());
        }

        var tenant = mapper.toTenant(organizationId, input);
        var savedTenant = repository.save(tenant);

        return mapper.toOutput(savedTenant);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantOutput getTenant(UUID tenantId) {
        var tenant = findTenant(tenantId);

        return mapper.toOutput(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantOutput> getTenants(UUID organizationId) {
        validateOrganizationId(organizationId);

        return repository.findAllByOrganizationId(organizationId)
                .stream()
                .map(mapper::toOutput)
                .toList();
    }

    @Override
    @Transactional
    public TenantOutput updateTenant(UUID tenantId, TenantUpdateInput input) {
        validateUpdateInput(input);

        var tenant = findTenant(tenantId);
        if (!tenant.getSlug().equals(input.slug())
                && repository.existsByOrganizationIdAndSlug(tenant.getOrganizationId(), input.slug())) {
            throw new ResourceAlreadyExistsException(TENANT, "slug", input.slug());
        }

        var updatedTenant = mapper.updateTenant(tenant, input);
        var savedTenant = repository.save(updatedTenant);

        return mapper.toOutput(savedTenant);
    }

    @Override
    @Transactional
    public TenantOutput changeTenantStatus(UUID tenantId, TenantStatus status) {
        if (status == null) {
            throw new InvalidRequestException("Tenant status must not be null");
        }

        var tenant = findTenant(tenantId);
        var updatedTenant = mapper.changeStatus(tenant, status);
        var savedTenant = repository.save(updatedTenant);

        return mapper.toOutput(savedTenant);
    }

    @Override
    @Transactional
    public void deleteTenant(UUID tenantId) {
        findTenant(tenantId);
        repository.deleteById(tenantId);
    }

    private Tenant findTenant(UUID tenantId) {
        return repository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(TENANT, "id", tenantId));
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

    private void validateInput(TenantInput input) {
        if (input == null) {
            throw new InvalidRequestException("Tenant input must not be null");
        }

        validateMutableFields(input.name(), input.slug(), input.environment());

        if (input.issuer() == null || input.issuer().isBlank()) {
            throw new InvalidRequestException("Tenant issuer must not be null or blank");
        }
    }

    private void validateUpdateInput(TenantUpdateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Tenant update input must not be null");
        }

        validateMutableFields(input.name(), input.slug(), input.environment());
    }

    private void validateMutableFields(String name, String slug, TenantEnvironment environment) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("Tenant name must not be null or blank");
        }

        if (slug == null || slug.isBlank()) {
            throw new InvalidRequestException("Tenant slug must not be null or blank");
        }

        if (environment == null) {
            throw new InvalidRequestException("Tenant environment must not be null");
        }
    }
}

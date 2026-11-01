package janus.tenancy.organization.application.port.usecase;

import janus.tenancy.organization.application.port.usecase.model.OrganizationInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.domain.type.OrganizationStatus;

import java.util.List;
import java.util.UUID;

public interface OrganizationUseCase {
    OrganizationOutput createOrganization(OrganizationInput input);
    OrganizationOutput getOrganization(UUID organizationId);
    List<OrganizationOutput> getOrganizations();
    OrganizationOutput updateOrganization(UUID organizationId, OrganizationInput input);
    OrganizationOutput changeOrganizationStatus(UUID organizationId, OrganizationStatus status);
    void deleteOrganization(UUID organizationId);
}

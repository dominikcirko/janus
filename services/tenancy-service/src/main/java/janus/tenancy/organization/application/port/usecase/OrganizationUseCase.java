package janus.tenancy.organization.application.port.usecase;

import janus.tenancy.organization.application.port.usecase.model.OrganizationCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationUpdateInput;
import janus.tenancy.organization.domain.type.OrganizationStatus;

import java.util.List;
import java.util.UUID;

public interface OrganizationUseCase {
    OrganizationOutput createOrganization(OrganizationCreateInput input);
    OrganizationOutput getOrganization(UUID organizationId);
    List<OrganizationOutput> getOrganizations();
    OrganizationOutput updateOrganization(UUID organizationId, OrganizationUpdateInput input);
    OrganizationOutput changeOrganizationStatus(UUID organizationId, OrganizationStatus status);
    void deleteOrganization(UUID organizationId);
}

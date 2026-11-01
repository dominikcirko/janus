package janus.tenancy.organization.application.port.usecase;

import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipUpdateInput;

import java.util.List;
import java.util.UUID;

public interface OrganizationMembershipUseCase {

    OrganizationMembershipOutput createOrganizationMembership(UUID organizationId, OrganizationMembershipCreateInput input);
    OrganizationMembershipOutput getOrganizationMembership(UUID membershipId);
    List<OrganizationMembershipOutput> getOrganizationMemberships(UUID organizationId);
    OrganizationMembershipOutput updateOrganizationMembership(UUID membershipId, OrganizationMembershipUpdateInput input);
    void deleteOrganizationMembership(UUID membershipId);
}

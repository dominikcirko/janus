package janus.tenancy.organization.testdata;

import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipResponse;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipUpdateRequest;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembership;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationMembershipUpdateInput;
import janus.tenancy.organization.domain.type.OrganizationRole;

import java.util.UUID;

public final class OrganizationMembershipTestDataFactory {

    public static final UUID TEST_MEMBERSHIP_ID = UUID.fromString("75f69fae-3580-4b94-b54d-cd368eb13467");
    public static final UUID TEST_USER_ID = UUID.fromString("5417ab51-f4f9-497d-aa5b-85bf3a764aad");

    private OrganizationMembershipTestDataFactory() {
    }

    public static OrganizationMembershipCreateInput createValidInput() {
        return new OrganizationMembershipCreateInput(TEST_USER_ID, OrganizationRole.ADMIN);
    }

    public static OrganizationMembershipCreateInput createInputWithUserId(UUID userId) {
        return new OrganizationMembershipCreateInput(userId, OrganizationRole.ADMIN);
    }

    public static OrganizationMembershipCreateInput createInputWithRole(OrganizationRole role) {
        return new OrganizationMembershipCreateInput(TEST_USER_ID, role);
    }

    public static OrganizationMembershipUpdateInput createValidUpdateInput() {
        return new OrganizationMembershipUpdateInput(OrganizationRole.OWNER);
    }

    public static OrganizationMembershipUpdateInput createUpdateInputWithRole(OrganizationRole role) {
        return new OrganizationMembershipUpdateInput(role);
    }

    public static OrganizationMembership createNewMembership() {
        return createMembership(null, OrganizationRole.ADMIN);
    }

    public static OrganizationMembership createMembership() {
        return createMembership(TEST_MEMBERSHIP_ID, OrganizationRole.ADMIN);
    }

    public static OrganizationMembership createUpdatedMembership() {
        return createMembership(TEST_MEMBERSHIP_ID, OrganizationRole.OWNER);
    }

    public static OrganizationMembershipOutput createOutput() {
        return createOutput(OrganizationRole.ADMIN);
    }

    public static OrganizationMembershipOutput createUpdatedOutput() {
        return createOutput(OrganizationRole.OWNER);
    }

    public static OrganizationMembershipRequest createValidRequest() {
        return new OrganizationMembershipRequest(TEST_USER_ID, OrganizationRole.ADMIN);
    }

    public static OrganizationMembershipUpdateRequest createValidUpdateRequest() {
        return new OrganizationMembershipUpdateRequest(OrganizationRole.OWNER);
    }

    public static OrganizationMembershipResponse createResponse() {
        return createResponse(OrganizationRole.ADMIN);
    }

    public static OrganizationMembershipResponse createUpdatedResponse() {
        return createResponse(OrganizationRole.OWNER);
    }

    private static OrganizationMembership createMembership(UUID id, OrganizationRole role) {
        return OrganizationMembership.builder()
                .id(id)
                .organizationId(OrganizationTestDataFactory.TEST_ORGANIZATION_ID)
                .userId(TEST_USER_ID)
                .role(role)
                .build();
    }

    private static OrganizationMembershipOutput createOutput(OrganizationRole role) {
        return new OrganizationMembershipOutput(
                TEST_MEMBERSHIP_ID,
                OrganizationTestDataFactory.TEST_ORGANIZATION_ID,
                TEST_USER_ID,
                role
        );
    }

    private static OrganizationMembershipResponse createResponse(OrganizationRole role) {
        return new OrganizationMembershipResponse(
                TEST_MEMBERSHIP_ID,
                OrganizationTestDataFactory.TEST_ORGANIZATION_ID,
                TEST_USER_ID,
                role
        );
    }
}

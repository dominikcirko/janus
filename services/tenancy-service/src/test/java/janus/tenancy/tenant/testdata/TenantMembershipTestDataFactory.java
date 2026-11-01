package janus.tenancy.tenant.testdata;

import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipResponse;
import janus.tenancy.tenant.adapter.in.web.dto.TenantMembershipUpdateRequest;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipUpdateInput;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.domain.type.TenantRole;

import java.util.Map;
import java.util.UUID;

public final class TenantMembershipTestDataFactory {

    public static final UUID TEST_MEMBERSHIP_ID = UUID.fromString("48e78ffc-0659-4b86-8a6c-771ddfd9b9be");
    public static final UUID TEST_USER_ID = UUID.fromString("e047c87c-b89f-4516-91a8-991658f5aaca");

    private static final Map<String, Object> TEST_METADATA = Map.of("department", "engineering");
    private static final Map<String, Object> UPDATED_METADATA = Map.of("department", "platform");

    private TenantMembershipTestDataFactory() {
    }

    public static TenantMembershipCreateInput createValidInput() {
        return new TenantMembershipCreateInput(TEST_USER_ID, TenantRole.MEMBER, TEST_METADATA);
    }

    public static TenantMembershipCreateInput createInputWithUserId(UUID userId) {
        return new TenantMembershipCreateInput(userId, TenantRole.MEMBER, TEST_METADATA);
    }

    public static TenantMembershipCreateInput createInputWithRole(TenantRole role) {
        return new TenantMembershipCreateInput(TEST_USER_ID, role, TEST_METADATA);
    }

    public static TenantMembershipCreateInput createInputWithMetadata(Map<String, Object> metadata) {
        return new TenantMembershipCreateInput(TEST_USER_ID, TenantRole.MEMBER, metadata);
    }

    public static TenantMembershipUpdateInput createValidUpdateInput() {
        return new TenantMembershipUpdateInput(TenantRole.ADMIN, UPDATED_METADATA);
    }

    public static TenantMembershipUpdateInput createUpdateInputWithRole(TenantRole role) {
        return new TenantMembershipUpdateInput(role, UPDATED_METADATA);
    }

    public static TenantMembershipUpdateInput createUpdateInputWithMetadata(Map<String, Object> metadata) {
        return new TenantMembershipUpdateInput(TenantRole.ADMIN, metadata);
    }

    public static TenantMembership createNewActiveMembership() {
        return createMembership(null, TenantRole.MEMBER, TenantMembershipStatus.ACTIVE, TEST_METADATA);
    }

    public static TenantMembership createActiveMembership() {
        return createMembership(
                TEST_MEMBERSHIP_ID,
                TenantRole.MEMBER,
                TenantMembershipStatus.ACTIVE,
                TEST_METADATA
        );
    }

    public static TenantMembership createUpdatedMembership() {
        return createMembership(
                TEST_MEMBERSHIP_ID,
                TenantRole.ADMIN,
                TenantMembershipStatus.ACTIVE,
                UPDATED_METADATA
        );
    }

    public static TenantMembership createSuspendedMembership() {
        return createMembership(
                TEST_MEMBERSHIP_ID,
                TenantRole.MEMBER,
                TenantMembershipStatus.SUSPENDED,
                TEST_METADATA
        );
    }

    public static TenantMembershipOutput createActiveOutput() {
        return createOutput(TenantRole.MEMBER, TenantMembershipStatus.ACTIVE, TEST_METADATA);
    }

    public static TenantMembershipOutput createUpdatedOutput() {
        return createOutput(TenantRole.ADMIN, TenantMembershipStatus.ACTIVE, UPDATED_METADATA);
    }

    public static TenantMembershipOutput createSuspendedOutput() {
        return createOutput(TenantRole.MEMBER, TenantMembershipStatus.SUSPENDED, TEST_METADATA);
    }

    public static TenantMembershipRequest createValidRequest() {
        return new TenantMembershipRequest(TEST_USER_ID, TenantRole.MEMBER, TEST_METADATA);
    }

    public static TenantMembershipUpdateRequest createValidUpdateRequest() {
        return new TenantMembershipUpdateRequest(TenantRole.ADMIN, UPDATED_METADATA);
    }

    public static TenantMembershipResponse createActiveResponse() {
        return createResponse(TenantRole.MEMBER, TenantMembershipStatus.ACTIVE, TEST_METADATA);
    }

    public static TenantMembershipResponse createUpdatedResponse() {
        return createResponse(TenantRole.ADMIN, TenantMembershipStatus.ACTIVE, UPDATED_METADATA);
    }

    public static TenantMembershipResponse createSuspendedResponse() {
        return createResponse(TenantRole.MEMBER, TenantMembershipStatus.SUSPENDED, TEST_METADATA);
    }

    private static TenantMembership createMembership(
            UUID id, TenantRole role, TenantMembershipStatus status, Map<String, Object> metadata) {
        return TenantMembership.builder()
                .id(id)
                .tenantId(TenantTestDataFactory.TEST_TENANT_ID)
                .userId(TEST_USER_ID)
                .role(role)
                .status(status)
                .metadata(metadata)
                .build();
    }

    private static TenantMembershipOutput createOutput(
            TenantRole role, TenantMembershipStatus status, Map<String, Object> metadata) {
        return new TenantMembershipOutput(
                TEST_MEMBERSHIP_ID,
                TenantTestDataFactory.TEST_TENANT_ID,
                TEST_USER_ID,
                role,
                status,
                metadata
        );
    }

    private static TenantMembershipResponse createResponse(
            TenantRole role, TenantMembershipStatus status, Map<String, Object> metadata) {
        return new TenantMembershipResponse(
                TEST_MEMBERSHIP_ID,
                TenantTestDataFactory.TEST_TENANT_ID,
                TEST_USER_ID,
                role,
                status,
                metadata
        );
    }
}

package janus.tenancy.organization.testdata;

import janus.tenancy.organization.adapter.in.web.dto.OrganizationRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationResponse;
import janus.tenancy.organization.application.port.usecase.model.Organization;
import janus.tenancy.organization.application.port.usecase.model.OrganizationCreateInput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationOutput;
import janus.tenancy.organization.application.port.usecase.model.OrganizationUpdateInput;
import janus.tenancy.organization.domain.type.OrganizationStatus;

import java.util.UUID;

public final class OrganizationTestDataFactory {

    public static final UUID TEST_ORGANIZATION_ID = UUID.fromString("2fcb3407-f457-4d86-a0af-a8c2612f2434");

    private static final String TEST_ORGANIZATION_NAME = "Test Organization";
    private static final String TEST_ORGANIZATION_SLUG = "test-organization";
    private static final String UPDATED_ORGANIZATION_NAME = "Updated Organization";
    private static final String UPDATED_ORGANIZATION_SLUG = "updated-organization";

    private OrganizationTestDataFactory() {
    }

    public static OrganizationCreateInput createValidInput() {
        return new OrganizationCreateInput(TEST_ORGANIZATION_NAME, TEST_ORGANIZATION_SLUG);
    }

    public static OrganizationUpdateInput createUpdatedInput() {
        return new OrganizationUpdateInput(UPDATED_ORGANIZATION_NAME, UPDATED_ORGANIZATION_SLUG);
    }

    public static OrganizationCreateInput createInputWithName(String name) {
        return new OrganizationCreateInput(name, TEST_ORGANIZATION_SLUG);
    }

    public static OrganizationCreateInput createInputWithSlug(String slug) {
        return new OrganizationCreateInput(TEST_ORGANIZATION_NAME, slug);
    }

    public static Organization createActiveOrganization() {
        return Organization.builder()
                .id(TEST_ORGANIZATION_ID)
                .name(TEST_ORGANIZATION_NAME)
                .slug(TEST_ORGANIZATION_SLUG)
                .status(OrganizationStatus.ACTIVE)
                .build();
    }

    public static OrganizationOutput createActiveOutput() {
        return new OrganizationOutput(
                TEST_ORGANIZATION_ID,
                TEST_ORGANIZATION_NAME,
                TEST_ORGANIZATION_SLUG,
                OrganizationStatus.ACTIVE
        );
    }

    public static OrganizationOutput createSuspendedOutput() {
        return new OrganizationOutput(
                TEST_ORGANIZATION_ID,
                TEST_ORGANIZATION_NAME,
                TEST_ORGANIZATION_SLUG,
                OrganizationStatus.SUSPENDED
        );
    }

    public static OrganizationRequest createValidRequest() {
        return new OrganizationRequest(TEST_ORGANIZATION_NAME, TEST_ORGANIZATION_SLUG);
    }

    public static OrganizationResponse createActiveResponse() {
        return new OrganizationResponse(
                TEST_ORGANIZATION_ID,
                TEST_ORGANIZATION_NAME,
                TEST_ORGANIZATION_SLUG,
                OrganizationStatus.ACTIVE
        );
    }

    public static OrganizationResponse createSuspendedResponse() {
        return new OrganizationResponse(
                TEST_ORGANIZATION_ID,
                TEST_ORGANIZATION_NAME,
                TEST_ORGANIZATION_SLUG,
                OrganizationStatus.SUSPENDED
        );
    }
}

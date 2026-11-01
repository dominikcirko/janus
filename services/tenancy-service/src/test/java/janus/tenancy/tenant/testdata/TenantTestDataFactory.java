package janus.tenancy.tenant.testdata;

import janus.tenancy.organization.testdata.OrganizationTestDataFactory;
import janus.tenancy.tenant.adapter.in.web.dto.TenantRequest;
import janus.tenancy.tenant.adapter.in.web.dto.TenantResponse;
import janus.tenancy.tenant.adapter.in.web.dto.TenantUpdateRequest;
import janus.tenancy.tenant.application.port.usecase.model.Tenant;
import janus.tenancy.tenant.application.port.usecase.model.TenantInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantUpdateInput;
import janus.tenancy.tenant.domain.type.TenantEnvironment;
import janus.tenancy.tenant.domain.type.TenantStatus;

import java.util.UUID;

public final class TenantTestDataFactory {

    public static final UUID TEST_TENANT_ID = UUID.fromString("547f2234-6c19-4b31-81e5-cbe4d184e0ca");

    private static final String TEST_TENANT_NAME = "Test Tenant";
    private static final String TEST_TENANT_SLUG = "test-tenant";
    private static final String TEST_ISSUER = "https://janus.example.com/test-tenant";
    private static final String UPDATED_TENANT_NAME = "Updated Tenant";
    private static final String UPDATED_TENANT_SLUG = "updated-tenant";

    private TenantTestDataFactory() {
    }

    public static TenantInput createValidInput() {
        return new TenantInput(
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TEST_ISSUER
        );
    }

    public static TenantInput createInputWithName(String name) {
        return new TenantInput(name, TEST_TENANT_SLUG, TenantEnvironment.DEVELOPMENT, TEST_ISSUER);
    }

    public static TenantInput createInputWithSlug(String slug) {
        return new TenantInput(TEST_TENANT_NAME, slug, TenantEnvironment.DEVELOPMENT, TEST_ISSUER);
    }

    public static TenantInput createInputWithEnvironment(TenantEnvironment environment) {
        return new TenantInput(TEST_TENANT_NAME, TEST_TENANT_SLUG, environment, TEST_ISSUER);
    }

    public static TenantInput createInputWithIssuer(String issuer) {
        return new TenantInput(TEST_TENANT_NAME, TEST_TENANT_SLUG, TenantEnvironment.DEVELOPMENT, issuer);
    }

    public static TenantUpdateInput createValidUpdateInput() {
        return new TenantUpdateInput(
                UPDATED_TENANT_NAME,
                UPDATED_TENANT_SLUG,
                TenantEnvironment.STAGING
        );
    }

    public static Tenant createNewActiveTenant() {
        return createTenant(null, TEST_TENANT_NAME, TEST_TENANT_SLUG, TenantEnvironment.DEVELOPMENT, TenantStatus.ACTIVE);
    }

    public static Tenant createActiveTenant() {
        return createTenant(
                TEST_TENANT_ID,
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TenantStatus.ACTIVE
        );
    }

    public static Tenant createUpdatedTenant() {
        return createTenant(
                TEST_TENANT_ID,
                UPDATED_TENANT_NAME,
                UPDATED_TENANT_SLUG,
                TenantEnvironment.STAGING,
                TenantStatus.ACTIVE
        );
    }

    public static Tenant createSuspendedTenant() {
        return createTenant(
                TEST_TENANT_ID,
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TenantStatus.SUSPENDED
        );
    }

    public static TenantOutput createActiveOutput() {
        return createOutput(
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TenantStatus.ACTIVE
        );
    }

    public static TenantOutput createUpdatedOutput() {
        return createOutput(
                UPDATED_TENANT_NAME,
                UPDATED_TENANT_SLUG,
                TenantEnvironment.STAGING,
                TenantStatus.ACTIVE
        );
    }

    public static TenantOutput createSuspendedOutput() {
        return createOutput(
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TenantStatus.SUSPENDED
        );
    }

    public static TenantRequest createValidRequest() {
        return new TenantRequest(
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TEST_ISSUER
        );
    }

    public static TenantUpdateRequest createValidUpdateRequest() {
        return new TenantUpdateRequest(
                UPDATED_TENANT_NAME,
                UPDATED_TENANT_SLUG,
                TenantEnvironment.STAGING
        );
    }

    public static TenantResponse createActiveResponse() {
        return createResponse(
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TenantStatus.ACTIVE
        );
    }

    public static TenantResponse createUpdatedResponse() {
        return createResponse(
                UPDATED_TENANT_NAME,
                UPDATED_TENANT_SLUG,
                TenantEnvironment.STAGING,
                TenantStatus.ACTIVE
        );
    }

    public static TenantResponse createSuspendedResponse() {
        return createResponse(
                TEST_TENANT_NAME,
                TEST_TENANT_SLUG,
                TenantEnvironment.DEVELOPMENT,
                TenantStatus.SUSPENDED
        );
    }

    private static Tenant createTenant(
            UUID id, String name, String slug, TenantEnvironment environment, TenantStatus status) {
        return Tenant.builder()
                .id(id)
                .organizationId(OrganizationTestDataFactory.TEST_ORGANIZATION_ID)
                .name(name)
                .slug(slug)
                .environment(environment)
                .status(status)
                .issuer(TEST_ISSUER)
                .build();
    }

    private static TenantOutput createOutput(
            String name, String slug, TenantEnvironment environment, TenantStatus status) {
        return new TenantOutput(
                TEST_TENANT_ID,
                OrganizationTestDataFactory.TEST_ORGANIZATION_ID,
                name,
                slug,
                environment,
                status,
                TEST_ISSUER
        );
    }

    private static TenantResponse createResponse(
            String name, String slug, TenantEnvironment environment, TenantStatus status) {
        return new TenantResponse(
                TEST_TENANT_ID,
                OrganizationTestDataFactory.TEST_ORGANIZATION_ID,
                name,
                slug,
                environment,
                status,
                TEST_ISSUER
        );
    }
}

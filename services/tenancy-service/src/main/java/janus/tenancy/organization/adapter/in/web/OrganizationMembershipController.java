package janus.tenancy.organization.adapter.in.web;

import jakarta.validation.Valid;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipResponse;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationMembershipUpdateRequest;
import janus.tenancy.organization.adapter.in.web.mapper.OrganizationMembershipWebMapper;
import janus.tenancy.organization.application.port.usecase.OrganizationMembershipUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}")
@RequiredArgsConstructor
public class OrganizationMembershipController {

    private final OrganizationMembershipUseCase organizationMembershipUseCase;
    private final OrganizationMembershipWebMapper organizationMembershipWebMapper;

    @PostMapping(
            value = "/organizations/{organizationId}/memberships",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrganizationMembershipResponse> createOrganizationMembership(
            @PathVariable UUID organizationId, @Valid @RequestBody OrganizationMembershipRequest request) {
        var input = organizationMembershipWebMapper.toInput(request);
        var output = organizationMembershipUseCase.createOrganizationMembership(organizationId, input);
        var response = organizationMembershipWebMapper.toResponse(output);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(
            value = "/organization-memberships/{membershipId}",
            version = "1+",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrganizationMembershipResponse getOrganizationMembership(@PathVariable UUID membershipId) {
        var output = organizationMembershipUseCase.getOrganizationMembership(membershipId);

        return organizationMembershipWebMapper.toResponse(output);
    }

    @GetMapping(
            value = "/organizations/{organizationId}/memberships",
            version = "1+",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<OrganizationMembershipResponse> getOrganizationMemberships(@PathVariable UUID organizationId) {
        var output = organizationMembershipUseCase.getOrganizationMemberships(organizationId);

        return organizationMembershipWebMapper.toResponse(output);
    }

    @PutMapping(
            value = "/organization-memberships/{membershipId}",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrganizationMembershipResponse updateOrganizationMembership(
            @PathVariable UUID membershipId, @Valid @RequestBody OrganizationMembershipUpdateRequest request) {
        var input = organizationMembershipWebMapper.toInput(request);
        var output = organizationMembershipUseCase.updateOrganizationMembership(membershipId, input);

        return organizationMembershipWebMapper.toResponse(output);
    }

    @DeleteMapping(value = "/organization-memberships/{membershipId}", version = "1+")
    public ResponseEntity<Void> deleteOrganizationMembership(@PathVariable UUID membershipId) {
        organizationMembershipUseCase.deleteOrganizationMembership(membershipId);

        return ResponseEntity.noContent().build();
    }
}

package janus.tenancy.organization.adapter.in.web;

import jakarta.validation.Valid;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationRequest;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationResponse;
import janus.tenancy.organization.adapter.in.web.dto.OrganizationStatusRequest;
import janus.tenancy.organization.adapter.in.web.mapper.OrganizationWebMapper;
import janus.tenancy.organization.application.port.usecase.OrganizationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{version}/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationUseCase organizationUseCase;
    private final OrganizationWebMapper organizationWebMapper;

    @PostMapping(
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody OrganizationRequest request) {
        var input = organizationWebMapper.toCreateInput(request);
        var output = organizationUseCase.createOrganization(input);
        var response = organizationWebMapper.toResponse(output);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping(value = "/{organizationId}", version = "1+", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrganizationResponse getOrganization(@PathVariable UUID organizationId) {
        var output = organizationUseCase.getOrganization(organizationId);

        return organizationWebMapper.toResponse(output);
    }

    @GetMapping(version = "1+", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrganizationResponse> getOrganizations() {
        var output = organizationUseCase.getOrganizations();

        return organizationWebMapper.toResponse(output);
    }

    @PutMapping(
            value = "/{organizationId}",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrganizationResponse updateOrganization(
            @PathVariable UUID organizationId,
            @Valid @RequestBody OrganizationRequest request) {
        var input = organizationWebMapper.toUpdateInput(request);
        var output = organizationUseCase.updateOrganization(organizationId, input);

        return organizationWebMapper.toResponse(output);
    }

    @PatchMapping(
            value = "/{organizationId}/status",
            version = "1+",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public OrganizationResponse changeOrganizationStatus(
            @PathVariable UUID organizationId,
            @Valid @RequestBody OrganizationStatusRequest request) {
        var output = organizationUseCase.changeOrganizationStatus(organizationId, request.status());

        return organizationWebMapper.toResponse(output);
    }

    @DeleteMapping(value = "/{organizationId}", version = "1+")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID organizationId) {
        organizationUseCase.deleteOrganization(organizationId);

        return ResponseEntity.noContent().build();
    }
}

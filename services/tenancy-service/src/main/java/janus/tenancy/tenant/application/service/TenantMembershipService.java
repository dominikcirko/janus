package janus.tenancy.tenant.application.service;

import janus.common.exceptionhandler.exception.InvalidRequestException;
import janus.common.exceptionhandler.exception.ResourceAlreadyExistsException;
import janus.common.exceptionhandler.exception.ResourceNotFoundException;
import janus.tenancy.tenant.application.mapper.TenantMembershipMapper;
import janus.tenancy.tenant.application.port.out.TenantMembershipRepository;
import janus.tenancy.tenant.application.port.out.TenantRepository;
import janus.tenancy.tenant.application.port.usecase.TenantMembershipUseCase;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembership;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipCreateInput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipOutput;
import janus.tenancy.tenant.application.port.usecase.model.TenantMembershipUpdateInput;
import janus.tenancy.tenant.domain.type.TenantMembershipStatus;
import janus.tenancy.tenant.domain.type.TenantRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantMembershipService implements TenantMembershipUseCase {

    private static final String TENANT = "Tenant";
    private static final String TENANT_MEMBERSHIP = "Tenant membership";

    private final TenantMembershipRepository repository;
    private final TenantRepository tenantRepository;
    private final TenantMembershipMapper mapper;

    @Override
    @Transactional
    public TenantMembershipOutput createTenantMembership(UUID tenantId, TenantMembershipCreateInput input) {
        validateTenantId(tenantId);
        validateInput(input);
        validateTenantExists(tenantId);

        if (repository.existsByTenantIdAndUserId(tenantId, input.userId())) {
            throw new ResourceAlreadyExistsException(TENANT_MEMBERSHIP, "userId", input.userId());
        }

        var membership = mapper.toTenantMembership(tenantId, input);
        var savedMembership = repository.save(membership);

        return mapper.toOutput(savedMembership);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantMembershipOutput getTenantMembership(UUID membershipId) {
        var membership = findTenantMembership(membershipId);

        return mapper.toOutput(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantMembershipOutput> getTenantMemberships(UUID tenantId) {
        validateTenantId(tenantId);

        return repository.findAllByTenantId(tenantId)
                .stream()
                .map(mapper::toOutput)
                .toList();
    }

    @Override
    @Transactional
    public TenantMembershipOutput updateTenantMembership(UUID membershipId, TenantMembershipUpdateInput input) {
        validateUpdateInput(input);

        var membership = findTenantMembership(membershipId);
        var updatedMembership = mapper.toUpdatedTenantMembership(membership, input);
        var savedMembership = repository.save(updatedMembership);

        return mapper.toOutput(savedMembership);
    }

    @Override
    @Transactional
    public TenantMembershipOutput changeTenantMembershipStatus(UUID membershipId, TenantMembershipStatus status) {
        if (status == null) {
            throw new InvalidRequestException("Tenant membership status must not be null");
        }

        var membership = findTenantMembership(membershipId);
        var updatedMembership = mapper.withUpdatedStatus(membership, status);
        var savedMembership = repository.save(updatedMembership);

        return mapper.toOutput(savedMembership);
    }

    @Override
    @Transactional
    public void deleteTenantMembership(UUID membershipId) {
        findTenantMembership(membershipId);
        repository.deleteById(membershipId);
    }

    private TenantMembership findTenantMembership(UUID membershipId) {
        return repository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException(TENANT_MEMBERSHIP, "id", membershipId));
    }

    private void validateTenantExists(UUID tenantId) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(TENANT, "id", tenantId));
    }

    private void validateTenantId(UUID tenantId) {
        if (tenantId == null) {
            throw new InvalidRequestException("Tenant id must not be null");
        }
    }

    private void validateInput(TenantMembershipCreateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Tenant membership input must not be null");
        }

        if (input.userId() == null) {
            throw new InvalidRequestException("Tenant membership user id must not be null");
        }

        validateMutableFields(input.role(), input.metadata());
    }

    private void validateUpdateInput(TenantMembershipUpdateInput input) {
        if (input == null) {
            throw new InvalidRequestException("Tenant membership update input must not be null");
        }

        validateMutableFields(input.role(), input.metadata());
    }

    private void validateMutableFields(TenantRole role, Map<String, Object> metadata) {
        if (role == null) {
            throw new InvalidRequestException("Tenant membership role must not be null");
        }

        if (metadata == null) {
            throw new InvalidRequestException("Tenant membership metadata must not be null");
        }
    }
}

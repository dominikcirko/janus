package janus.tenancy.organization.application.port.out;

import janus.tenancy.organization.application.port.usecase.model.Organization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {

    Organization save(Organization organization);
    Optional<Organization> findById(UUID id);
    Optional<Organization> findBySlug(String slug);
    List<Organization> findAll();
    boolean existsBySlug(String slug);
    void deleteById(UUID id);
}

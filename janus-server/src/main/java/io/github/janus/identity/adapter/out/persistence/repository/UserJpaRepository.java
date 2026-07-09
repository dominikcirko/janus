package io.github.janus.identity.adapter.out.persistence.repository;

import io.github.janus.identity.adapter.out.persistence.entity.UserEntity;
import io.github.janus.identity.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @Query("""
        select u from UserEntity u
        where u.email = :email
        and u.tenant.id = :tenantId
    """)
    UserEntity findByEmail(String email, UUID tenantId);

    @Query("""
        select u from UserEntity u
        where u.id = :id
        and u.tenant.id = :tenantId
    """)
    UserEntity findById(UUID id, UUID tenantId);

    @Query("""
        select u from UserEntity u
        where u.id = :id
        and u.tenant.id = :tenantId
    """)
    UserEntity findByUsername(String username, UUID tenantId);

    @Modifying
    @Query("""
        delete from UserEntity u 
        where u.id = :id 
        and u.tenant.id = :tenantId
    """)
    void deleteById(UUID id, UUID tenantId);

    List<UserEntity> findAllByTenantId(UUID tenantId);
}

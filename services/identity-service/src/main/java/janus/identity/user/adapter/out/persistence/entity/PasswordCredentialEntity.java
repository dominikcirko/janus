package janus.identity.user.adapter.out.persistence.entity;

import jakarta.persistence.*;
import janus.common.AbstractMappedIdEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "user_password")
public class PasswordCredentialEntity extends AbstractMappedIdEntity {

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false, updatable = false)
    private UserEntity user;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "password_changed_at", nullable = false)
    private Instant passwordChangedAt;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;
}

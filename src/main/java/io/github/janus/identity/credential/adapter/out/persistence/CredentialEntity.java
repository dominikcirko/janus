package io.github.janus.identity.credential.adapter.out.persistence;

import io.github.janus.common.AbstractEntity;
import io.github.janus.identity.credential.domain.type.CredentialType;
import io.github.janus.identity.user.adapter.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@Table(name = "credential")
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CredentialEntity extends AbstractEntity {

    @Column
    @Enumerated(EnumType.STRING)
    private CredentialType type;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}

package io.github.janus.identity.adapter.out.persistence.entity;

import io.github.janus.common.AbstractEntity;
import io.github.janus.identity.domain.type.CredentialType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "credentials")
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsEntity extends AbstractEntity {

    @Column
    @Enumerated(EnumType.STRING)
    private CredentialType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}

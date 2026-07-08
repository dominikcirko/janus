package io.github.janus.identity;

import io.github.janus.common.AbstractEntity;
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
    private CredentialType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}

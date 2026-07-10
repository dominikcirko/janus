package io.github.janus.identity.systemrole.adapter.out.persistence;

import io.github.janus.common.AbstractEntity;
import io.github.janus.identity.systemrole.domain.type.SystemRoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@Table(name = "system_role")
@EqualsAndHashCode(callSuper = true)
public class SystemRoleEntity extends AbstractEntity {

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private SystemRoleType type;

}

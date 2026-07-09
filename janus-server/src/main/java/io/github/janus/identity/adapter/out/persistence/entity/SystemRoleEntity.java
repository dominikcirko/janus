package io.github.janus.identity.adapter.out.persistence.entity;

import io.github.janus.common.AbstractEntity;
import io.github.janus.identity.domain.type.SystemRoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "system_role")
@EqualsAndHashCode(callSuper = true)
public class SystemRoleEntity extends AbstractEntity {

    @Column
    @Enumerated(EnumType.STRING)
    private SystemRoleType type;

}

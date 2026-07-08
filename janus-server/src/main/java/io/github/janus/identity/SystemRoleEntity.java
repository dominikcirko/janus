package io.github.janus.identity;

import io.github.janus.common.AbstractEntity;
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

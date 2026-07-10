package io.github.janus.identity.adapter.out.persistence.entity;


import io.github.janus.common.AbstractEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Table(name = "permission")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionEntity extends AbstractEntity {

    @Column
    private String name;
}

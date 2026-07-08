package io.github.janus.customer;

import io.github.janus.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerEntity extends AbstractEntity {

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

}

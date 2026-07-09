package io.github.janus.customer;

import io.github.janus.common.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "customer")
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

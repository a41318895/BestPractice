package com.akichou.mysqlwithjpa.entity.relation;

import com.akichou.mysqlwithjpa.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "documentation")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Documentation extends AuditableEntity {

    @Column(name = "documentation_name", nullable = false)
    @EqualsAndHashCode.Include
    private String name ;

    @OneToOne(mappedBy = "documentation")
    private Chapter chapter ;

    @Version
    private Long version = 0L ;  // For optimistic locking
}

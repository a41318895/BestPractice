package com.akichou.mysqlwithjpa.entity;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends AuditableEntity {

    @Column(name = "product_name", nullable = false, unique = true, length = 64)
    @EqualsAndHashCode.Include
    private String productName ;

    @Column(name = "description", nullable = true, unique = true, length = 255)
    @EqualsAndHashCode.Include
    private String description = "" ;

    @Version
    private Long version = 0L ;  // For optimistic locking
}

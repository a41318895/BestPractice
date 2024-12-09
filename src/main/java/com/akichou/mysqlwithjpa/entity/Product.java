package com.akichou.mysqlwithjpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "product_name", nullable = false, unique = true, length = 64)
    private String productName ;

    @Column(name = "description", nullable = true, unique = true, length = 255)
    private String description = "" ;

    @Version
    private Long version = 0L ;  // For optimistic locking
}

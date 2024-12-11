package com.akichou.mysqlwithjpa.entity.relation;

import com.akichou.mysqlwithjpa.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "chapter")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chapter extends AuditableEntity {

    @Column(name = "chapter_title", nullable = false)
    @EqualsAndHashCode.Include
    private String title ;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course ;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "documentation_id")
    private Documentation documentation ;

    @Version
    private Long version = 0L ;  // For optimistic locking
}

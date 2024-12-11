package com.akichou.mysqlwithjpa.entity.relation;

import com.akichou.mysqlwithjpa.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "author")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Author extends AuditableEntity {

    @Column(name = "author_name", nullable = false)
    @EqualsAndHashCode.Include
    private String name ;

    @Column(name = "author_email", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String email ;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "author_course",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>() ;

    @Version
    private Long version = 0L ;  // For optimistic locking

    public void addCourse(Course course) {

        if (this.courses == null) courses = new ArrayList<>() ;

        if (!this.courses.contains(course)) {

            courses.add(course) ;

            log.info("Added course {} into author {}." , course.getName(), this.getName()) ;

            course.addAuthor(this) ;
        }
    }

    public void removeCourse(Course course) {

        if (this.courses != null) {

            courses.remove(course) ;

            log.info("Removed course {} from author {}." , course.getName(), this.getName()) ;

            course.removeAuthor(this) ;
        }
    }

    public int getCourseNumber() {

        return this.courses != null ? this.courses.size() : 0 ;
    }

    public void clearCourses() {

        if (this.courses != null) {

            List<Course> coursesCopy = new ArrayList<>(this.courses) ;

            // Avoid ConcurrentModificationException occurring while modifying operations in collection iteration.
            coursesCopy.forEach(this::removeCourse) ;

            log.info("Clearing courses from author {}." , this.getName()) ;
        }
    }
}

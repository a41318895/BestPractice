package com.akichou.mysqlwithjpa.entity.relation;

import com.akichou.mysqlwithjpa.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List ;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "course")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Course extends AuditableEntity {

    @Column(name = "course_name", nullable = false)
    @EqualsAndHashCode.Include
    private String name ;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Author> authors = new ArrayList<>() ;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Chapter> chapters = new ArrayList<>() ;

    @Version
    private Long version = 0L ;  // For optimistic locking

    public void addAuthor(Author author) {

        if (this.authors == null) authors = new ArrayList<>() ;

        if (!this.authors.contains(author)) {

            this.authors.add(author) ;

            log.info("Added author {} into course {}.", author.getName(), this.getName()) ;

            author.addCourse(this) ;
        }
    }

    public void removeAuthor(Author author) {

        if (this.authors != null) {

            authors.remove(author) ;

            log.info("Removed author {} from course {}.", author.getName(), this.getName()) ;

            author.getCourses().remove(this) ;
        }
    }

    public void addChapter(Chapter chapter) {

        if (this.chapters == null) chapters = new ArrayList<>() ;

        if (!this.chapters.contains(chapter)) {

            this.chapters.add(chapter) ;

            log.info("Added chapter {} into course {}.", chapter.getTitle(), this.getName()) ;

            chapter.setCourse(this) ;
        }
    }

    public void removeChapter(Chapter chapter) {

        if (this.chapters != null) {

            chapters.remove(chapter) ;

            log.info("Removed chapter {} from course {}.", chapter.getTitle(), this.getName()) ;

            chapter.setCourse(null) ;
        }
    }

    public int getChapterNumber() {

        return this.chapters != null ? this.chapters.size() : 0 ;
    }

    public int getAuthorNumber() {

        return this.authors != null ? this.authors.size() : 0 ;
    }

    public boolean hasSpecificAuthor(Author author) {

        return this.authors != null && this.authors.contains(author) ;
    }

    public void clearAuthors() {

        if (this.authors != null) {

            List<Author> authorsCopy = new ArrayList<>(this.authors) ;

            // Avoid ConcurrentModificationException occurring
            authorsCopy.forEach(this::removeAuthor) ;

            log.info("Clearing authors from course {}.", this.getName()) ;
        }
    }

    public void clearChapters() {

        if (this.chapters != null) {

            List<Chapter> chaptersCopy = new ArrayList<>(this.chapters) ;

            // Avoid ConcurrentModificationException occurring
            chaptersCopy.forEach(this::removeChapter) ;

            log.info("Clearing chapters from course {}.", this.getName()) ;
        }
    }
}

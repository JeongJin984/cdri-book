package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "book",
    schema = "cdri_books",
    uniqueConstraints = @UniqueConstraint(columnNames = {"title", "author"}),
    indexes = {
        @Index(name = "idx_book_category", columnList = "category_id"),
        @Index(name = "idx_book_status", columnList = "status")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 255)
    private String author;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryJpaEntity category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected BookJpaEntity(String title, String author, BookStatus status, CategoryJpaEntity category) {
        this.title = title;
        this.author = author;
        this.status = status;
        this.category = category;
    }

    public static BookJpaEntity of(String title, String author, BookStatus status, CategoryJpaEntity category) {
        return new BookJpaEntity(title, author, status, category);
    }

    public void changeCategory(CategoryJpaEntity category) {
        this.category = category;
    }
}

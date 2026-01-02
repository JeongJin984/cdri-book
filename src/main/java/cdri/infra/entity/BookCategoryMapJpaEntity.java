package cdri.infra.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "book_category_map",
    schema = "cdri_books",
    uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "category_id"}),
    indexes = {
        @Index(name = "idx_book_category_map_book_id", columnList = "book_id, created_at"),
        @Index(name = "idx_bcm_category_book", columnList = "category_id, book_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class BookCategoryMapJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long bookCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, updatable = false)
    private BookJpaEntity book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, updatable = false)
    private CategoryJpaEntity category;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Nonnull
    public static BookCategoryMapJpaEntity of(BookJpaEntity book, CategoryJpaEntity category) {
        if (book == null) throw new IllegalArgumentException("book must not be null");
        if (category == null) throw new IllegalArgumentException("category must not be null");
        if (category.getCategoryId() == null) throw new IllegalArgumentException("Category must be persisted");

        BookCategoryMapJpaEntity map = new BookCategoryMapJpaEntity();
        map.book = book;
        map.category = category;
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookCategoryMapJpaEntity other)) return false;
        if (this.bookCategoryId == null || other.bookCategoryId == null) return false;
        return this.bookCategoryId.equals(other.bookCategoryId);
    }

    @Override
    public int hashCode() {
        return (bookCategoryId != null) ? bookCategoryId.hashCode() : System.identityHashCode(this);
    }
}

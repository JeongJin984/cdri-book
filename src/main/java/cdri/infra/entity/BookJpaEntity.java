package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import cdri.common.exception.NoBookCategoryException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(
    name = "book",
    schema = "cdri_books",
    uniqueConstraints = @UniqueConstraint(columnNames = {"title", "author"}),
    indexes = {
        @Index(name = "idx_book_status", columnList = "status")
    }
)
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.PROTECTED)
    private Set<BookCategoryMapJpaEntity> categories = new LinkedHashSet<>();

    protected BookJpaEntity(String title, String author, BookStatus status) {
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public static BookJpaEntity of(String title, String author, BookStatus status, List<CategoryJpaEntity> categories) {
        if(categories == null || categories.isEmpty()) throw new NoBookCategoryException("category must not be null");
        BookJpaEntity book = new BookJpaEntity(title, author, status);
        book.syncCategories(categories);
        return book;
    }

    public void syncCategories(List<CategoryJpaEntity> categories) {
        if (categories == null || categories.isEmpty())
            throw new NoBookCategoryException("category must not be null");

        Set<Long> targetIds = new HashSet<>();
        for (CategoryJpaEntity c : categories) {
            if (c == null) throw new NoBookCategoryException("category must not be null");
            Long id = c.getCategoryId();
            if (id == null) throw new IllegalStateException("Category must be persisted before adding to Book");
            targetIds.add(id);
        }

        // 1) 제거: target에 없는 기존 매핑 제거 (orphanRemoval=true => delete 발생)
        this.categories.removeIf(m -> !targetIds.contains(m.getCategory().getCategoryId()));

        // 2) 추가: 이미 남아있는 것 제외하고 추가
        Set<Long> existingIds = this.categories.stream()
            .map(m -> m.getCategory().getCategoryId())
            .collect(Collectors.toSet());

        for (CategoryJpaEntity c : categories) {
            if (c == null) throw new NoBookCategoryException("category must not be null");
            Long id = c.getCategoryId();
            if (existingIds.add(id)) {
                this.categories.add(BookCategoryMapJpaEntity.of(this, c));
            }
        }
    }

    public void changeCategory(List<CategoryJpaEntity> categories) {
        if(categories == null || categories.isEmpty()) throw new NoBookCategoryException("category must not be null");
        syncCategories(categories);
    }
}

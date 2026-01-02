package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import cdri.common.exception.NoBookCategoryException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
    private final LinkedHashSet<BookCategoryMapJpaEntity> categories = new LinkedHashSet<>();

    protected BookJpaEntity(String title, String author, BookStatus status) {
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public static BookJpaEntity of(String title, String author, BookStatus status, List<CategoryJpaEntity> categories) {
        if(categories == null || categories.isEmpty()) throw new NoBookCategoryException("category must not be null");
        BookJpaEntity book = new BookJpaEntity(title, author, status);
        book.addAllCategories(categories);
        return book;
    }

    public void addAllCategories(List<CategoryJpaEntity> categories) {
        if (categories == null || categories.isEmpty())
            throw new NoBookCategoryException("category must not be null");

        // 기존에 이미 연결된 categoryId들
        Set<Long> existing = this.categories.stream()
            .map(m -> m.getCategory().getCategoryId())
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // 새로 들어온 것 중, existing에 없는 것만 추가
        for (CategoryJpaEntity c : categories) {
            if (c == null) continue;

            Long id = c.getCategoryId();
            if (id == null) throw new IllegalStateException("Category must be persisted before adding to Book");

            if (existing.add(id)) { // add가 true면 "원래 없던 id"라는 뜻
                this.categories.add(BookCategoryMapJpaEntity.of(this, c));
            }
        }
    }

    public void changeCategory(List<CategoryJpaEntity> categories) {
        if(categories == null || categories.isEmpty()) throw new NoBookCategoryException("category must not be null");
        this.categories.clear();
        addAllCategories(categories);
    }
}

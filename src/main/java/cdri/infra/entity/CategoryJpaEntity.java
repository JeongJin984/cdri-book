package cdri.infra.entity;

import cdri.common.enums.CategoryStatus;
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
import java.util.Collections;
import java.util.List;

@Entity
@Table(
    name = "category",
    schema = "cdri_books",
    uniqueConstraints = @UniqueConstraint(columnNames = {"name"}),
    indexes = {@Index(name = "idx_category_status", columnList = "status")}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    private Long categoryId;

    @Getter
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Getter
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private final List<BookJpaEntity> books = new ArrayList<>();

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public List<BookJpaEntity> getBooks() {
        return Collections.unmodifiableList(books);
    }

    protected CategoryJpaEntity(String name, CategoryStatus status) {
        this.name = name;
        this.status = status;
    }

    public static CategoryJpaEntity of(String name, CategoryStatus status) {
        return new CategoryJpaEntity(name, status);
    }
}

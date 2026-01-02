package cdri.infra.repository;

import cdri.common.enums.BookStatus;
import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.repository.BookRepository;
import cdri.infra.converter.bb.BookSearchPredicate;
import cdri.infra.entity.BookJpaEntity;
import cdri.infra.repository.adaptor.BookJpaRepository;
import cdri.infra.entity.QBookCategoryMapJpaEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cdri.infra.entity.QBookCategoryMapJpaEntity.bookCategoryMapJpaEntity;
import static cdri.infra.entity.QBookJpaEntity.bookJpaEntity;
import static cdri.infra.entity.QCategoryJpaEntity.categoryJpaEntity;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final JPAQueryFactory queryFactory;
    private final BookJpaRepository bookJpaRepository;

    @Override
    public List<BookSearchResult> findSliceWithCategoryByCommandKeyset(BookSearchCmd command, int size) {
        // 1) Book slice (정렬/커서/limit은 여기서만)
        List<BookRow> bookRows = queryFactory
            .select(Projections.constructor(
                BookRow.class,
                bookJpaEntity.bookId,
                bookJpaEntity.title,
                bookJpaEntity.author,
                bookJpaEntity.status,
                bookJpaEntity.createdAt
            ))
            .from(bookJpaEntity)
            .where(BookSearchPredicate.from(command)) // byCategoryId는 EXISTS 권장 (아래 참고)
            .orderBy(bookJpaEntity.createdAt.desc(), bookJpaEntity.bookId.desc())
            .limit(size)
            .fetch();

        if (bookRows.isEmpty()) return Collections.emptyList();

        List<Long> bookIds = bookRows.stream().map(BookRow::bookId).toList();

        // 2) 카테고리 로딩
        List<CategoryRow> categoryRows = queryFactory
            .select(Projections.constructor(
                CategoryRow.class,
                bookCategoryMapJpaEntity.book.bookId,
                categoryJpaEntity.categoryId,
                categoryJpaEntity.name,
                categoryJpaEntity.createdAt
            ))
            .from(bookCategoryMapJpaEntity)
            .join(bookCategoryMapJpaEntity.category, categoryJpaEntity)
            .where(bookCategoryMapJpaEntity.book.bookId.in(bookIds))
            .fetch();

        // bookId -> categories
        Map<Long, List<BookSearchResult.BookCategory>> categoriesByBookId =
            categoryRows.stream()
                .collect(Collectors.groupingBy(
                    CategoryRow::bookId,
                    Collectors.mapping(
                        r -> new BookSearchResult.BookCategory(
                            r.categoryId(),
                            r.categoryName(),
                            r.categoryCreatedAt()
                        ),
                        Collectors.toList()
                    )
                ));

        // 최종 조립: 1번 쿼리 순서 유지(정렬/limit 정합성 유지)
        return bookRows.stream()
            .map(b -> new BookSearchResult(
                categoriesByBookId.getOrDefault(b.bookId(), List.of()),
                new BookSearchResult.Book(
                    b.bookId(),
                    b.title(),
                    b.author(),
                    b.status(),
                    b.createdAt()
                )
            ))
            .toList();
    }

    @Override
    public Optional<BookJpaEntity> findById(Long id) {
        BookJpaEntity book = queryFactory
            .selectFrom(bookJpaEntity)
            .leftJoin(bookJpaEntity.categories, bookCategoryMapJpaEntity).fetchJoin()
            .leftJoin(bookCategoryMapJpaEntity.category, categoryJpaEntity).fetchJoin()
            .where(bookJpaEntity.bookId.eq(id))
            .fetchOne();
        return Optional.ofNullable(book);
    }

    /**
     * Book 1차 조회용 row
     */
    public record BookRow(
        Long bookId,
        String title,
        String author,
        BookStatus status,
        LocalDateTime createdAt
    ) {}

    /**
     * Category 2차 조회용 row
     */
    public record CategoryRow(
        Long bookId,
        Long categoryId,
        String categoryName,
        LocalDateTime categoryCreatedAt
    ) {}
}

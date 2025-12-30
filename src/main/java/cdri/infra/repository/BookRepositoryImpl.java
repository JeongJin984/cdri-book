package cdri.infra.repository;

import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.repository.BookRepository;
import cdri.infra.converter.bb.BookSearchPredicate;
import cdri.infra.entity.BookJpaEntity;
import cdri.infra.repository.adaptor.BookJpaRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static cdri.infra.entity.QBookJpaEntity.bookJpaEntity;
import static cdri.infra.entity.QCategoryJpaEntity.categoryJpaEntity;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final JPAQueryFactory queryFactory;
    private final BookJpaRepository bookJpaRepository;

    @Override
    public List<BookJpaEntity> findSliceWithCategoryByCommandKeyset(BookSearchCmd command, int size) {
        return queryFactory
            .selectFrom(bookJpaEntity)
            .join(bookJpaEntity.category, categoryJpaEntity).fetchJoin()
            .where(BookSearchPredicate.from(command))
            .orderBy(bookJpaEntity.createdAt.desc(), bookJpaEntity.bookId.desc())
            .limit(size)
            .fetch();
    }

    @Override
    public Optional<BookJpaEntity> findById(Long id) {
        BookJpaEntity book = queryFactory
            .selectFrom(bookJpaEntity)
            .join(bookJpaEntity.category, categoryJpaEntity).fetchJoin()
            .where(bookJpaEntity.bookId.eq(id))
            .fetchOne();
        return Optional.ofNullable(book);
    }
}

package cdri.infra.converter.bb;

import cdri.common.enums.BookStatus;
import cdri.domain.dto.command.BookSearchCommand;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.NoArgsConstructor;

import static cdri.infra.entity.QBookJpaEntity.bookJpaEntity;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BookSearchPredicate {
    public static BooleanExpression[] from(BookSearchCommand command) {
        return new BooleanExpression[] {
            byBookName(command),
            byCategoryId(command),
            byAuthorName(command),
            byStatus(command),
            cursorPredicate(command.cursor())
        };
    }

    private static BooleanExpression byBookName(BookSearchCommand command) {
        return command.bookName() == null ? null : bookJpaEntity.title.containsIgnoreCase(command.bookName());
    }

    private static BooleanExpression byCategoryId(BookSearchCommand command) {
        return command.categoryId() == null ? null : bookJpaEntity.category.categoryId.eq(command.categoryId());
    }

    private static BooleanExpression byAuthorName(BookSearchCommand command) {
        return command.authorName() == null ? null : bookJpaEntity.author.containsIgnoreCase(command.authorName());
    }

    private static BooleanExpression byStatus(BookSearchCommand command) {
        if (command.canBorrow() == null) return null;
        return command.canBorrow()
            ? bookJpaEntity.status.in(BookStatus.borrowableStatuses())
            : bookJpaEntity.status.in(BookStatus.nonBorrowableStatuses());
    }

    private static BooleanExpression cursorPredicate(BookSearchCommand.BookCursor cursor) {
        if (cursor == null || cursor.createdAt() == null || cursor.bookId() == null) return null;
        return bookJpaEntity.createdAt.lt(cursor.createdAt())
            .or(bookJpaEntity.createdAt.eq(cursor.createdAt())
                .and(bookJpaEntity.bookId.lt(cursor.bookId())));
    }
}

package cdri.domain.dto.command;

import cdri.common.enums.BookStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookSearchCommand (
    String bookName,
    Long categoryId,
    String authorName,
    Boolean canBorrow,
    BookCursor cursor
) {

    public record BookCursor(LocalDateTime createdAt, Long bookId) {}
}
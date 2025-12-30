package cdri.domain.dto.command;

import java.time.LocalDateTime;

public record BookSearchCmd(
    String bookName,
    Long categoryId,
    String authorName,
    Boolean canBorrow,
    BookCursor cursor
) {

    public record BookCursor(LocalDateTime createdAt, Long bookId) {}
}
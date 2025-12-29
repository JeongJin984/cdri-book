package cdri.domain.dto.result;

import cdri.common.enums.BookStatus;

import java.time.LocalDateTime;

public record CategoryModifyResult(
    BookCategory category,
    Book book
) {
    public record BookCategory (
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt
    ) {}

    public record Book(
        Long bookId,
        String bookTitle,
        String authorName,
        BookStatus status,
        LocalDateTime createdAt
    ) {}
}

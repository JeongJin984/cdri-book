package cdri.domain.dto.result;

import cdri.common.enums.BookStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookSearchResult (
    List<BookCategory> categories,
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

package cdri.domain.dto.result;

import cdri.common.enums.BookStatus;

import java.time.LocalDateTime;

public record BookSearchResult (
    String categoryName,
    Long bookId,
    String bookTitle,
    String authorName,
    BookStatus status,
    LocalDateTime createdAt
) {

}

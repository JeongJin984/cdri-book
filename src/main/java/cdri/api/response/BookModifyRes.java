package cdri.api.response;

import java.time.LocalDateTime;

public record BookModifyRes(
    Long categoryId,
    String categoryName,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    LocalDateTime bookCreatedAt
) {
}

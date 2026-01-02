package cdri.api.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookModifyRes(
    List<Category> categories,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    LocalDateTime bookCreatedAt
) {
    public record Category(
        Long categoryId,
        String categoryName
    ) {}
}

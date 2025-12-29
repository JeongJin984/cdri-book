package cdri.api.response;

import java.time.LocalDateTime;

public record BookSearchRes (
    String category,
    Long bookId,
    String title,
    String author,
    LocalDateTime createdAt
) {
}

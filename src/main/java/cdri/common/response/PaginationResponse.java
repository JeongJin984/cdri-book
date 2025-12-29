package cdri.common.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public record PaginationResponse<T> (
    List<T> items,
    NextCursor nextCursor,
    boolean hasNext
) {
    public record NextCursor(
        LocalDateTime createdAt,
        Long bookId
    ) {}


}

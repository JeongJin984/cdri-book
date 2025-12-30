package cdri.api.controller;

import cdri.api.response.BookSearchRes;
import cdri.common.exception.InvalidCursorException;
import cdri.common.response.PaginationResponse;
import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.service.BookSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
public class BookSearchApi {
    private final BookSearchService bookSearchService;
    private final Clock clock;

    @GetMapping
    public PaginationResponse<BookSearchRes> searchBooks(
        @RequestParam(name = "bookName", required = false) String bookName,
        @RequestParam(name = "categoryId", required = false) Long categoryId,
        @RequestParam(name = "authorName", required = false) String authorName,
        @RequestParam(name = "canBorrow", required = false) Boolean canBorrow,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime cursorCreatedAt,
        @RequestParam(required = false) Long cursorBookId,

        @RequestParam(name = "size", required = false, defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        BookSearchCmd.BookCursor cursor = parseCursor(cursorCreatedAt, cursorBookId);

        List<BookSearchResult> fetched = bookSearchService.searchBooks(
            new BookSearchCmd(bookName, categoryId, authorName, canBorrow, cursor),
            size + 1
        );

        boolean hasNext = fetched.size() > size;
        List<BookSearchResult> items = hasNext ? fetched.subList(0, size) : fetched;

        PaginationResponse.NextCursor nextCursor = items.isEmpty()
            ? null
            : new PaginationResponse.NextCursor(items.getLast().book().createdAt(),
            items.getLast().book().bookId());

        return new PaginationResponse<>(
            items.stream().map(v -> new BookSearchRes(
                v.category().categoryId(),
                v.category().categoryName(),
                v.book().bookId(),
                v.book().bookTitle(),
                v.book().authorName(),
                v.book().createdAt()
            )).toList(),
            nextCursor,
            hasNext
        );
    }

    private BookSearchCmd.BookCursor parseCursor(LocalDateTime cursorCreatedAt, Long cursorBookId) {
        boolean hasAt = cursorCreatedAt != null;
        boolean hasId = cursorBookId != null;

        if (cursorCreatedAt != null && cursorCreatedAt.isAfter(LocalDateTime.now(clock))) {
            throw new InvalidCursorException("cursorCreatedAt must be before now.");
        }

        if (hasAt != hasId) { // XOR
            throw new InvalidCursorException("cursorCreatedAt and cursorBookId must be both present or both absent.");
        }
        return hasAt ? new BookSearchCmd.BookCursor(cursorCreatedAt, cursorBookId) : null;
    }
}

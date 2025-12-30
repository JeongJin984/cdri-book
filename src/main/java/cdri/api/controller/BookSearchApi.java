package cdri.api.controller;

import cdri.api.response.BookSearchRes;
import cdri.common.exception.InvalidCursorException;
import cdri.common.response.ExceptionResponse;
import cdri.common.response.PaginationResponse;
import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.service.BookSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "책 검색", description = "책 검색 api")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Validated
public class BookSearchApi {
    private final BookSearchService bookSearchService;
    private final Clock clock;

    @Operation(summary = "책 조회", description = "조회 조건을 바탕으로 책을 페이지네이션하여 함께 검색한다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(
            responseCode = "400",
            description = "실패",
            content = @Content(
                examples = {
                    @ExampleObject(value = """
                    {
                      "errCode": "string",
                      "errMsg": "string"
                    }
                    """)
                },
                schema = @Schema(implementation = ExceptionResponse.class)
            )
        )
    })
    @GetMapping
    public PaginationResponse<BookSearchRes> searchBooks(
        @Parameter(description = "Book title to search for")
        @RequestParam(name = "bookName", required = false) String bookName,
        @Parameter(description = "Category ID to filter by")
        @RequestParam(name = "categoryId", required = false) Long categoryId,
        @Parameter(description = "Author name to search for")
        @RequestParam(name = "authorName", required = false) String authorName,
        @Parameter(description = "Filter by availability")
        @RequestParam(name = "canBorrow", required = false) Boolean canBorrow,

        @Parameter(description = "Cursor for pagination (creation date time)")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime cursorCreatedAt,
        @Parameter(description = "Cursor for pagination (book ID)")
        @RequestParam(required = false) Long cursorBookId,

        @Parameter(description = "Page size")
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

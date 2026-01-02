package cdri.api.controller;

import cdri.api.request.BookCategoryModifyReq;
import cdri.api.response.BookModifyRes;
import cdri.api.response.BookSearchRes;
import cdri.common.response.ExceptionResponse;
import cdri.domain.dto.command.BookCategoryModifyCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.dto.result.CategoryModifyResult;
import cdri.domain.service.BookModifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "책 정보 수정", description = "책 정보 수정 api")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookModifyApi {
    private final BookModifyService bookModifyService;

    @Operation(summary = "책 카테고리 수정", description = "책의 카테고리를 수정한다.")
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
    @PostMapping("/{book_id}")
    public BookModifyRes modifyBook(
        @PathVariable(name = "book_id") Long bookId,
        @Valid @RequestBody BookCategoryModifyReq req
    ) {
        CategoryModifyResult modifyResult = bookModifyService.modifyBookCategory(
            bookId, new BookCategoryModifyCmd(req.categoryId())
        );

        return new BookModifyRes(
            modifyResult.categories().stream()
                .map(c -> new BookModifyRes.Category(c.categoryId(), c.categoryName()))
                .toList(),
            modifyResult.book().bookId(),
            modifyResult.book().bookTitle(),
            modifyResult.book().authorName(),
            modifyResult.book().createdAt()
        );
    }
}

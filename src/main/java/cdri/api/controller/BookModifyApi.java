package cdri.api.controller;

import cdri.api.request.BookCategoryModifyReq;
import cdri.api.response.BookModifyRes;
import cdri.api.response.BookSearchRes;
import cdri.domain.dto.command.BookCategoryModifyCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.dto.result.CategoryModifyResult;
import cdri.domain.service.BookModifyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookModifyApi {
    private final BookModifyService bookModifyService;

    @PostMapping("/{book_id}")
    public BookModifyRes modifyBook(
        @PathVariable(name = "book_id") Long bookId,
        @Valid @RequestBody BookCategoryModifyReq req
    ) {
        CategoryModifyResult modifyResult = bookModifyService.modifyBookCategory(
            bookId, new BookCategoryModifyCmd(req.categoryId())
        );

        return new BookModifyRes(
            modifyResult.category().categoryId(),
            modifyResult.category().categoryName(),
            modifyResult.book().bookId(),
            modifyResult.book().bookTitle(),
            modifyResult.book().authorName(),
            modifyResult.book().createdAt()
        );
    }
}

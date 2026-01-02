package cdri.api.controller;

import cdri.api.request.BookCategoryModifyReq;
import cdri.common.enums.BookStatus;
import cdri.common.exception.NoSuchBookException;
import cdri.domain.dto.result.CategoryModifyResult;
import cdri.domain.service.BookModifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static cdri.common.enums.ResponseCode.INVALID_REQUEST;
import static cdri.common.enums.ResponseCode.NO_BOOK_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookModifyApi.class)
class BookModifyApiTest {

    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookModifyService bookModifyService;

    @Test
    @DisplayName("책 카테고리 수정 API 성공")
    void modifyBook_success() throws Exception {
        // given
        long bookId = 1L;
        BookCategoryModifyReq req = new BookCategoryModifyReq(List.of(2L));
        CategoryModifyResult result = new CategoryModifyResult(
            List.of(new CategoryModifyResult.BookCategory(2L, "Novel", FIXED_NOW)),
            new CategoryModifyResult.Book(1L, "Spring", "Author", BookStatus.OK, FIXED_NOW)
        );

        given(bookModifyService.modifyBookCategory(eq(bookId), any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/books/{bookId}", bookId)
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookTitle").value("Spring"))
            .andExpect(jsonPath("$.categories[0].categoryName").value("Novel"));
    }

    @Test
    @DisplayName("존재하지 않는 책일 경우 400 에러")
    void modifyBook_notFound() throws Exception {
        // given
        long bookId = 1L;
        BookCategoryModifyReq req = new BookCategoryModifyReq(List.of(2L));

        given(bookModifyService.modifyBookCategory(eq(bookId), any())).willThrow(new NoSuchBookException("No book found"));

        // when & then
        mockMvc.perform(post("/api/v1/books/{bookId}", bookId)
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(NO_BOOK_FOUND.name()));
    }

    @Test
    @DisplayName("잘못된 요청(categoryId 누락)일 경우 400 에러")
    void modifyBook_invalidRequest() throws Exception {
        // given
        long bookId = 1L;
        String reqBody = "{}";

        // when & then
        mockMvc.perform(post("/api/v1/books/{bookId}", bookId)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_REQUEST.name()));
    }

    @Test
    @DisplayName("잘못된 요청(categoryId가 1보다 작음)일 경우 400 에러")
    void modifyBook_invalidCategoryId_tooSmall() throws Exception {
        // given
        long bookId = 1L;
        // In reality, @Min(1) on List<Long> might not work as expected for the elements, 
        // but let's assume it's for the list size or some other validation if it was intended.
        // Actually @Min(1) on a List usually doesn't validate elements.
        // But the requirement says "categoryId가 1보다 작음".
        // Let's see how the validator is set up.
        BookCategoryModifyReq req = new BookCategoryModifyReq(List.of(0L));

        // when & then
        mockMvc.perform(post("/api/v1/books/{bookId}", bookId)
                .content(objectMapper.writeValueAsString(req))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_REQUEST.name()));
    }
}
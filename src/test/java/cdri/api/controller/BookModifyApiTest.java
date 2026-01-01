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
        Long bookId = 1L;
        Long categoryId = 2L;
        BookCategoryModifyReq req = new BookCategoryModifyReq(categoryId);
        LocalDateTime now = LocalDateTime.now();

        CategoryModifyResult.BookCategory category = new CategoryModifyResult.BookCategory(categoryId, "New Category", now);
        CategoryModifyResult.Book book = new CategoryModifyResult.Book(bookId, "Title", "Author", BookStatus.OK, now);
        CategoryModifyResult result = new CategoryModifyResult(category, book);

        given(bookModifyService.modifyBookCategory(eq(bookId), any())).willReturn(result);

        // when & then
        mockMvc.perform(post("/api/v1/books/{book_id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookId").value(bookId))
            .andExpect(jsonPath("$.categoryId").value(categoryId))
            .andExpect(jsonPath("$.categoryName").value("New Category"));
    }

    @Test
    @DisplayName("존재하지 않는 책일 경우 400 에러")
    void modifyBook_notFound() throws Exception {
        // given
        Long bookId = 999L;
        BookCategoryModifyReq req = new BookCategoryModifyReq(2L);

        given(bookModifyService.modifyBookCategory(eq(bookId), any()))
            .willThrow(new NoSuchBookException("Not Found"));

        // when & then
        mockMvc.perform(post("/api/v1/books/{book_id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(NO_BOOK_FOUND.name()));
    }

    @Test
    @DisplayName("잘못된 요청(categoryId 누락)일 경우 400 에러")
    void modifyBook_invalidRequest() throws Exception {
        // given
        Long bookId = 1L;
        String invalidJson = "{}";

        // when & then
        mockMvc.perform(post("/api/v1/books/{book_id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_REQUEST.name()));
    }

    @Test
    @DisplayName("잘못된 요청(categoryId가 1보다 작음)일 경우 400 에러")
    void modifyBook_invalidCategoryId_tooSmall() throws Exception {
        // given
        Long bookId = 1L;
        BookCategoryModifyReq req = new BookCategoryModifyReq(0L);

        // when & then
        mockMvc.perform(post("/api/v1/books/{book_id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_REQUEST.name()));
    }
}
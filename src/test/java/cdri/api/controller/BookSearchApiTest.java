package cdri.api.controller;

import cdri.common.enums.BookStatus;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.service.BookSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static cdri.common.enums.ResponseCode.INVALID_CURSOR;
import static cdri.common.enums.ResponseCode.INVALID_REQUEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookSearchApi.class)
@Import(BookSearchApiTest.TestClockConfig.class)
class BookSearchApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookSearchService bookSearchService;

    static class TestClockConfig {
        @org.springframework.context.annotation.Bean
        public Clock clock() {
            return Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneId.of("UTC"));
        }
    }

    @Test
    @DisplayName("책 검색 API 성공")
    void searchBooks_success() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now(Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneId.of("UTC")));
        BookSearchResult.BookCategory category = new BookSearchResult.BookCategory(1L, "IT", now);
        BookSearchResult.Book book = new BookSearchResult.Book(1L, "Spring", "Author", BookStatus.OK, now);
        BookSearchResult result = new BookSearchResult(category, book);

        given(bookSearchService.searchBooks(any(), anyInt())).willReturn(List.of(result));

        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("bookName", "Spring")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items[0].bookTitle").value("Spring"))
            .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("커서 날짜가 현재보다 미래일 경우 400 에러")
    void searchBooks_invalidCursorDate() throws Exception {
        // given
        String futureDate = "2026-01-02T00:00:00";

        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("cursorCreatedAt", futureDate)
                .param("cursorBookId", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_CURSOR.name()));
    }

    @Test
    @DisplayName("커서 날짜와 ID 중 하나만 제공된 경우 400 에러")
    void searchBooks_incompleteCursor() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("cursorCreatedAt", "2025-12-31T23:59:59")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_CURSOR.name()));
    }

    @Test
    @DisplayName("page size가 100을 넘어가면 400 에러")
    void searchBooks_pageSizeTooBig() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("size", "101")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_REQUEST.name()));
    }
}
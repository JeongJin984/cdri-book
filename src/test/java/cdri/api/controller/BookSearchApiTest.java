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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookSearchApi.class)
@Import(BookSearchApiTest.TestClockConfig.class)
class BookSearchApiTest {

    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2026, 1, 1, 12, 0);

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
        BookSearchResult.BookCategory category = new BookSearchResult.BookCategory(1L, "IT", FIXED_NOW);
        BookSearchResult.Book book = new BookSearchResult.Book(1L, "Spring", "Author", BookStatus.OK, FIXED_NOW);
        BookSearchResult result = new BookSearchResult(List.of(category), book);

        given(bookSearchService.searchBooks(any(), anyInt())).willReturn(List.of(result));

        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("bookName", "Spring")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items[0].bookTitle").value("Spring"))
            .andExpect(jsonPath("$.items[0].categories[0].categoryName").value("IT"))
            .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("size 미지정 시 기본값 10이 적용되어 서비스에 size + 1인 11이 전달된다")
    void searchBooks_defaultSize() throws Exception {
        // given
        given(bookSearchService.searchBooks(any(), eq(11))).willReturn(List.of());

        // when
        mockMvc.perform(get("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // then
        verify(bookSearchService).searchBooks(any(), eq(11));
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
    @DisplayName("cursorCreatedAt 포맷이 잘못되었을 때 400 에러")
    void searchBooks_invalidCursorFormat() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("cursorCreatedAt", "2026/01/01")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errCode").value(INVALID_REQUEST.name()));
    }

    @Test
    @DisplayName("bookName이 빈 문자열일 때 400 에러 또는 전체 조회(현재는 서비스에 빈 문자열 전달)")
    void searchBooks_emptyBookName() throws Exception {
        // given
        given(bookSearchService.searchBooks(any(), anyInt())).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/books")
                .param("bookName", "")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // then (현재 스펙대로라면 호출 검증을 추가하는 게 테스트 가치가 큼)
        verify(bookSearchService).searchBooks(any(), anyInt());
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
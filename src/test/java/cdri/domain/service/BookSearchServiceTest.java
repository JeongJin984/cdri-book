package cdri.domain.service;

import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.repository.BookRepository;
import cdri.infra.entity.BookJpaEntity;
import cdri.infra.entity.CategoryJpaEntity;
import cdri.testutil.TestEntities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookSearchService bookSearchService;

    @Test
    @DisplayName("책 검색 성공")
    void searchBooks_success() {
        // given
        CategoryJpaEntity it = TestEntities.category(1L, "IT");
        BookJpaEntity book = TestEntities.book(1L, "Spring", "Author", List.of(it));
        BookSearchResult result = new BookSearchResult(
            List.of(new BookSearchResult.BookCategory(1L, "IT", FIXED_NOW)),
            new BookSearchResult.Book(1L, "Spring", "Author", book.getStatus(), FIXED_NOW)
        );

        given(bookRepository.findSliceWithCategoryByCommandKeyset(any(), anyInt())).willReturn(List.of(result));

        // when
        List<BookSearchResult> results = bookSearchService.searchBooks(new BookSearchCmd("Spring", null, null, null, null), 10);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().book().bookTitle()).isEqualTo("Spring");
    }

    @Test
    @DisplayName("검색 결과가 없을 때 빈 리스트 반환")
    void searchBooks_empty() {
        // given
        given(bookRepository.findSliceWithCategoryByCommandKeyset(any(), anyInt())).willReturn(List.of());

        // when
        List<BookSearchResult> results = bookSearchService.searchBooks(new BookSearchCmd("NonExistent", null, null, null, null), 10);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("BookSearchService가 size/cmd를 레포지토리에 그대로 전달하는지 확인")
    void searchBooks_verifyRepositoryCall() {
        // given
        BookSearchCmd cmd = new BookSearchCmd("Spring", 1L, "Author", true, null);
        int size = 10;
        given(bookRepository.findSliceWithCategoryByCommandKeyset(cmd, size)).willReturn(List.of());

        // when
        bookSearchService.searchBooks(cmd, size);

        // then
        verify(bookRepository).findSliceWithCategoryByCommandKeyset(cmd, size);
    }
}
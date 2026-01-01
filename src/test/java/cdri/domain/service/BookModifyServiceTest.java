package cdri.domain.service;

import cdri.common.exception.NoSuchBookException;
import cdri.common.exception.NoSuchCategoryException;
import cdri.domain.dto.command.BookCategoryModifyCmd;
import cdri.domain.dto.result.CategoryModifyResult;
import cdri.domain.repository.BookRepository;
import cdri.domain.repository.CategoryRepository;
import cdri.infra.entity.BookJpaEntity;
import cdri.infra.entity.CategoryJpaEntity;
import cdri.testutil.TestEntities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookModifyServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookModifyService bookModifyService;

    @Test
    @DisplayName("책 카테고리 수정 성공")
    void modifyBookCategory_success() {
        // given
        Long bookId = 1L;
        Long categoryId = 2L;
        BookCategoryModifyCmd cmd = new BookCategoryModifyCmd(categoryId);

        CategoryJpaEntity oldCategory = TestEntities.category(1L, "Old");
        CategoryJpaEntity newCategory = TestEntities.category(categoryId, "New");
        BookJpaEntity book = TestEntities.book(bookId, "Title", "Author", oldCategory);

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(newCategory));

        // when
        CategoryModifyResult result = bookModifyService.modifyBookCategory(bookId, cmd);

        // then
        assertThat(result.category().categoryId()).isEqualTo(categoryId);
        assertThat(result.category().categoryName()).isEqualTo("New");
        assertThat(book.getCategory()).isEqualTo(newCategory);
    }

    @Test
    @DisplayName("존재하지 않는 책일 경우 예외 발생")
    void modifyBookCategory_bookNotFound() {
        // given
        Long bookId = 1L;
        BookCategoryModifyCmd cmd = new BookCategoryModifyCmd(2L);
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookModifyService.modifyBookCategory(bookId, cmd))
            .isInstanceOf(NoSuchBookException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리일 경우 예외 발생")
    void modifyBookCategory_categoryNotFound() {
        // given
        Long bookId = 1L;
        Long categoryId = 2L;
        BookCategoryModifyCmd cmd = new BookCategoryModifyCmd(categoryId);

        CategoryJpaEntity category = TestEntities.category(1L, "IT");
        BookJpaEntity book = TestEntities.book(bookId, "Title", "Author", category);

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookModifyService.modifyBookCategory(bookId, cmd))
            .isInstanceOf(NoSuchCategoryException.class);
    }
}
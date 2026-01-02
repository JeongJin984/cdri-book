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

import java.util.List;
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
        long bookId = 1L;
        CategoryJpaEntity it = TestEntities.category(1L, "IT");
        BookJpaEntity book = TestEntities.book(bookId, "Spring", "Author", List.of(it));
        CategoryJpaEntity novel = TestEntities.category(2L, "Novel");

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(categoryRepository.findAllByIds(List.of(2L))).willReturn(List.of(novel));

        BookCategoryModifyCmd cmd = new BookCategoryModifyCmd(List.of(2L));

        // when
        CategoryModifyResult result = bookModifyService.modifyBookCategory(bookId, cmd);

        // then
        assertThat(result.book().bookTitle()).isEqualTo("Spring");
        assertThat(result.categories()).hasSize(1);
        assertThat(result.categories().getFirst().categoryName()).isEqualTo("Novel");
    }

    @Test
    @DisplayName("존재하지 않는 책일 경우 예외 발생")
    void modifyBookCategory_bookNotFound() {
        // given
        long bookId = 1L;
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        BookCategoryModifyCmd cmd = new BookCategoryModifyCmd(List.of(2L));

        // when & then
        assertThatThrownBy(() -> bookModifyService.modifyBookCategory(bookId, cmd))
            .isInstanceOf(NoSuchBookException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리일 경우 예외 발생")
    void modifyBookCategory_categoryNotFound() {
        // given
        long bookId = 1L;
        CategoryJpaEntity it = TestEntities.category(1L, "IT");
        BookJpaEntity book = TestEntities.book(bookId, "Spring", "Author", List.of(it));

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(categoryRepository.findAllByIds(List.of(2L, 3L))).willReturn(List.of(TestEntities.category(2L, "Novel")));

        BookCategoryModifyCmd cmd = new BookCategoryModifyCmd(List.of(2L, 3L));

        // when & then
        assertThatThrownBy(() -> bookModifyService.modifyBookCategory(bookId, cmd))
            .isInstanceOf(NoSuchCategoryException.class);
    }
}
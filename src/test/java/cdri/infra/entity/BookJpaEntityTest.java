package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import cdri.common.exception.NoBookCategoryException;
import cdri.testutil.TestEntities;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookJpaEntityTest {

    @Test
    @DisplayName("책 생성 시 카테고리가 포함되어야 한다")
    void createBook_withCategory() {
        // given
        CategoryJpaEntity category = TestEntities.category(1L, "IT");
        List<CategoryJpaEntity> categories = List.of(category);

        // when
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, categories);

        // then
        assertThat(book.getTitle()).isEqualTo("Spring");
        assertThat(book.getAuthor()).isEqualTo("Author");
        assertThat(book.getStatus()).isEqualTo(BookStatus.OK);
        assertThat(book.getCategories()).hasSize(1);
        assertThat(book.getCategories().stream().map(BookCategoryMapJpaEntity::getCategory).collect(Collectors.toSet()).containsAll(categories)).isTrue();
    }

    @Test
    @DisplayName("책 생성 시 카테고리가 없거나 null이면 예외가 발생한다")
    void createBook_withNullCategory_throwsException() {
        // when & then
        assertThatThrownBy(() -> BookJpaEntity.of("Spring", "Author", BookStatus.OK, null))
            .isInstanceOf(NoBookCategoryException.class);
        assertThatThrownBy(() -> BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of()))
            .isInstanceOf(NoBookCategoryException.class);
    }

    @Test
    @DisplayName("책의 카테고리를 변경할 수 있다")
    void changeCategory_success() {
        // given
        CategoryJpaEntity it = TestEntities.category(1L, "IT");
        BookJpaEntity book = TestEntities.book(2L, "Spring", "Author", List.of(it));
        CategoryJpaEntity novel = TestEntities.category(3L, "Novel");

        // when
        book.changeCategory(List.of(novel));

        // then
        assertThat(book.getCategories()).hasSize(1);
        assertThat(book.getCategories().iterator().next().getCategory()).isEqualTo(novel);
    }

    @Test
    @DisplayName("책의 카테고리를 null로 변경하려고 하면 예외가 발생한다")
    void changeCategory_withNull_throwsException() {
        // given
        CategoryJpaEntity it = TestEntities.category(1L, "IT");
        BookJpaEntity book = TestEntities.book(2L, "Spring", "Author", List.of(it));

        // when & then
        assertThatThrownBy(() -> book.changeCategory(null))
            .isInstanceOf(NoBookCategoryException.class);

        assertThatThrownBy(() -> book.changeCategory(List.of()))
            .isInstanceOf(NoBookCategoryException.class);
    }

    @Test
    @DisplayName("syncCategories()에 중복 카테고리 입력 시 첫 번째 것만 유지된다")
    void syncCategories_withDuplicate_keepsFirstOnly() {
        // given
        CategoryJpaEntity c1 = TestEntities.category(1L, "IT");
        CategoryJpaEntity c2 = TestEntities.category(1L, "IT_DUP");

        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of(c1));

        // when
        book.syncCategories(List.of(c1, c2));

        // then
        assertThat(book.getCategories()).hasSize(1);
        assertThat(book.getCategories().iterator().next().getCategory().getName()).isEqualTo("IT");
    }
}
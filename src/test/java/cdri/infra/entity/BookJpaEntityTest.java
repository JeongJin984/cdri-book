package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import cdri.common.enums.CategoryStatus;
import cdri.common.exception.NoBookCategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookJpaEntityTest {

    @Test
    @DisplayName("책 생성 시 카테고리가 포함되어야 한다")
    void createBook_withCategory() {
        // given
        CategoryJpaEntity category = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        List<CategoryJpaEntity> categories = List.of(category);

        // when
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, categories);

        // then
        assertThat(book.getTitle()).isEqualTo("Spring");
        assertThat(book.getAuthor()).isEqualTo("Author");
        assertThat(book.getStatus()).isEqualTo(BookStatus.OK);
        assertThat(book.getCategories()).hasSize(1);
        assertThat(book.getCategories().getFirst().getCategory()).isEqualTo(category);
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
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of(it));
        CategoryJpaEntity novel = CategoryJpaEntity.of("Novel", CategoryStatus.OK);

        // when
        book.changeCategory(List.of(novel));

        // then
        assertThat(book.getCategories()).hasSize(1);
        assertThat(book.getCategories().getFirst().getCategory()).isEqualTo(novel);
    }

    @Test
    @DisplayName("책의 카테고리를 null로 변경하려고 하면 예외가 발생한다")
    void changeCategory_withNull_throwsException() {
        // given
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of(it));

        // when & then
        assertThatThrownBy(() -> book.changeCategory(null))
            .isInstanceOf(NoBookCategoryException.class);

        assertThatThrownBy(() -> book.changeCategory(List.of()))
            .isInstanceOf(NoBookCategoryException.class);
    }

    @Test
    @DisplayName("addAllCategories()에 중복 카테고리 입력 시 첫 번째 것만 유지된다")
    void addAllCategories_withDuplicate_keepsFirstOnly() {
        // given
        CategoryJpaEntity c1 = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        setCategoryId(c1, 1L);
        CategoryJpaEntity c2 = CategoryJpaEntity.of("IT_DUP", CategoryStatus.OK);
        setCategoryId(c2, 1L); // 동일한 ID

        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of(c1));

        // when
        book.addAllCategories(List.of(c1, c2));

        // then
        // 기존 1개 + 추가 시도 (중복 제외되어 0개 추가됨) = 1개
        //  이미 있는 것과 중복되는 것을 넣었을 때의 동작을 확인)
        assertThat(book.getCategories()).hasSize(1);
        assertThat(book.getCategories().getFirst().getCategory().getName()).isEqualTo("IT");
    }

    private void setCategoryId(CategoryJpaEntity category, Long id) {
        try {
            java.lang.reflect.Field field = CategoryJpaEntity.class.getDeclaredField("categoryId");
            field.setAccessible(true);
            field.set(category, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

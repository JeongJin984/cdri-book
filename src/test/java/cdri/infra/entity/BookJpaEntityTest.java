package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import cdri.common.enums.CategoryStatus;
import cdri.common.exception.BookNeedsCategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookJpaEntityTest {

    @Test
    @DisplayName("책 생성 시 카테고리가 포함되어야 한다")
    void createBook_withCategory() {
        // given
        CategoryJpaEntity category = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        
        // when
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, category);
        
        // then
        assertThat(book.getCategory()).isNotNull();
        assertThat(book.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("책 생성 시 카테고리가 null이면 예외가 발생한다")
    void createBook_withNullCategory_throwsException() {
        // when & then
        assertThatThrownBy(() -> BookJpaEntity.of("Spring", "Author", BookStatus.OK, null))
            .isInstanceOf(BookNeedsCategoryException.class);
    }

    @Test
    @DisplayName("책의 카테고리를 변경할 수 있다")
    void changeCategory_success() {
        // given
        CategoryJpaEntity oldCategory = CategoryJpaEntity.of("Old", CategoryStatus.OK);
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, oldCategory);
        CategoryJpaEntity newCategory = CategoryJpaEntity.of("New", CategoryStatus.OK);

        // when
        book.changeCategory(newCategory);

        // then
        assertThat(book.getCategory()).isEqualTo(newCategory);
    }

    @Test
    @DisplayName("책의 카테고리를 null로 변경하려고 하면 예외가 발생한다")
    void changeCategory_withNull_throwsException() {
        // given
        CategoryJpaEntity category = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, category);

        // when & then
        assertThatThrownBy(() -> book.changeCategory(null))
            .isInstanceOf(BookNeedsCategoryException.class)
            .hasMessage("category must not be null");
    }
}

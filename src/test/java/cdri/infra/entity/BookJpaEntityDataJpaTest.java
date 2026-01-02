package cdri.infra.entity;

import cdri.common.enums.BookStatus;
import cdri.common.enums.CategoryStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(BookJpaEntityDataJpaTest.AuditingTestConfig.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.sql.init.mode=always",
    "spring.jpa.properties.hibernate.default_schema=cdri_books",
    "spring.jpa.properties.hibernate.hbm2ddl.create_namespaces=true"
})
class BookJpaEntityDataJpaTest {

    @TestConfiguration
    @EnableJpaAuditing
    static class AuditingTestConfig {
        // createdAt/updatedAt auditing 활성화 목적
    }

    @Autowired
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setSchema() {
        // schema.sql에서 CREATE SCHEMA + SET SCHEMA를 이미 하지만,
        // 테스트에서 JdbcTemplate로 조회할 때 안정성을 위해 명시적으로 맞춰둔다.
        jdbcTemplate.execute("SET SCHEMA cdri_books");
    }

    @Test
    @DisplayName("책 저장 성공 및 조회 확인: book, mapping row 생성 확인")
    void saveBook_success() {
        // given
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        em.persist(it);
        em.flush();

        BookJpaEntity book = BookJpaEntity.of("New Book", "Author", BookStatus.OK, List.of(it));

        // when
        em.persist(book);
        em.flush();
        em.clear();

        // then: 엔티티 조회
        BookJpaEntity found = em.find(BookJpaEntity.class, book.getBookId());
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("New Book");

        // then: 매핑 테이블 row로 검증(연관관계 LAZY/구현 영향 제거)
        Integer mapCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM book_category_map WHERE book_id = ?",
            Integer.class,
            book.getBookId()
        );
        assertThat(mapCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Auditing 동작: createdAt, updatedAt이 설정된다")
    void auditing_works() {
        // given
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        em.persist(it);
        em.flush();

        BookJpaEntity book = BookJpaEntity.of("Auditing Book", "Author", BookStatus.OK, List.of(it));

        // when
        em.persist(book);
        em.flush();

        // then
        assertThat(book.getCreatedAt()).isNotNull();
        assertThat(book.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("유니크 제약( title, author ): 중복 저장 시 제약 위반 예외")
    void uniqueConstraint_titleAuthor() {
        // given
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        em.persist(it);
        em.flush();

        BookJpaEntity book1 = BookJpaEntity.of("Duplicate", "Author", BookStatus.OK, List.of(it));
        em.persist(book1);
        em.flush();

        BookJpaEntity book2 = BookJpaEntity.of("Duplicate", "Author", BookStatus.OK, List.of(it));

        // when & then
        assertThatThrownBy(() -> {
            em.persist(book2);
            em.flush(); // 여기서 터져야 정상
        }).isInstanceOfAny(
            PersistenceException.class,
            DataIntegrityViolationException.class
        ).satisfies(ex -> {
            // 가능하면 원인까지 좁혀서 "유니크 제약 위반"임을 보장
            Throwable root = rootCause(ex);
            assertThat(root)
                .isInstanceOfAny(ConstraintViolationException.class, java.sql.SQLException.class);
        });
    }

    @Test
    @DisplayName("changeCategory(): 기존(IT) 매핑이 삭제되고 새(Novel) 매핑만 남는다")
    void changeCategory_orphanRemoval_works() {
        // given
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        CategoryJpaEntity novel = CategoryJpaEntity.of("Novel", CategoryStatus.OK);
        em.persist(it);
        em.persist(novel);
        em.flush();

        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of(it));
        em.persist(book);
        em.flush();

        Long bookId = book.getBookId();

        // 사전 검증: IT 매핑 존재
        assertThat(countMapping(bookId, it.getCategoryId())).isEqualTo(1);
        assertThat(countMapping(bookId, novel.getCategoryId())).isEqualTo(0);

        // when
        book.changeCategory(List.of(novel));
        em.flush();
        em.clear();

        // then: IT 매핑은 삭제(orphanRemoval), Novel만 존재
        assertThat(countMapping(bookId, it.getCategoryId())).isEqualTo(0);
        assertThat(countMapping(bookId, novel.getCategoryId())).isEqualTo(1);

        Integer total = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM book_category_map WHERE book_id = ?",
            Integer.class,
            bookId
        );
        assertThat(total).isEqualTo(1);
    }

    @Test
    @DisplayName("syncCategories(): 중복 카테고리를 넣어도 (book_id, category_id) 유니크로 row는 1개 유지 + 예외 없이 flush 성공")
    void syncCategories_duplicateCategory_doesNotCreateDuplicateRows() {
        // given
        CategoryJpaEntity it = CategoryJpaEntity.of("IT", CategoryStatus.OK);
        em.persist(it);
        em.flush();

        BookJpaEntity book = BookJpaEntity.of("Spring", "Author", BookStatus.OK, List.of(it));
        em.persist(book);
        em.flush();

        Long bookId = book.getBookId();

        // when
        book.syncCategories(List.of(it, it));

        // then: 예외 없이 flush 되어야 한다(내부 중복제거든, 동일 row 재생성이든 유니크 위반 없어야 함)
        em.flush();
        em.clear();

        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM book_category_map WHERE book_id = ? AND category_id = ?",
            Integer.class,
            bookId,
            it.getCategoryId()
        );
        assertThat(count).isEqualTo(1);

        Integer total = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM book_category_map WHERE book_id = ?",
            Integer.class,
            bookId
        );
        assertThat(total).isEqualTo(1);
    }

    private int countMapping(Long bookId, Long categoryId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM book_category_map WHERE book_id = ? AND category_id = ?",
            Integer.class,
            bookId,
            categoryId
        );
        return count == null ? 0 : count;
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }
}

package cdri.testutil;

import cdri.common.enums.BookStatus;
import cdri.common.enums.CategoryStatus;
import cdri.infra.entity.BookJpaEntity;
import cdri.infra.entity.CategoryJpaEntity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public final class TestEntities {
    private TestEntities() {}

    public static CategoryJpaEntity category(long id, String name) {
        CategoryJpaEntity c = CategoryJpaEntity.of(name, CategoryStatus.OK);
        set(c, "categoryId", id);
        set(c, "createdAt", LocalDateTime.of(2025, 1, 1, 0, 0));
        return c;
    }

    public static BookJpaEntity book(long id, String title, String author, CategoryJpaEntity category) {
        BookJpaEntity b = BookJpaEntity.of(title, author, BookStatus.OK, category);
        set(b, "bookId", id);
        set(b, "createdAt", LocalDateTime.of(2025, 1, 1, 0, 0));
        return b;
    }

    private static void set(Object target, String fieldName, Object value) {
        try {
            Field f = findField(target.getClass(), fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to set field '" + fieldName + "' on " + target.getClass(), e);
        }
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> cur = type;
        while (cur != null) {
            try {
                return cur.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                cur = cur.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
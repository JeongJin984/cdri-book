package cdri.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BookStatus {
    OK(true), LOST(false), DAMAGED(false);

    private final boolean borrowable;

    BookStatus(boolean borrowable) {
        this.borrowable = borrowable;
    }

    public static List<BookStatus> borrowableStatuses() {
        return Arrays.stream(values())
            .filter(BookStatus::isBorrowable)
            .toList();
    }

    public static List<BookStatus> nonBorrowableStatuses() {
        return Arrays.stream(values())
            .filter(s -> !s.isBorrowable())
            .toList();
    }
}

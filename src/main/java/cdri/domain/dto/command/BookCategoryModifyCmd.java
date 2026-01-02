package cdri.domain.dto.command;

import java.util.List;

public record BookCategoryModifyCmd (
    List<Long> categoryId
) {
}

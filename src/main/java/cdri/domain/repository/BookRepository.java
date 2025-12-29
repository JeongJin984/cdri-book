package cdri.domain.repository;

import cdri.domain.dto.command.BookSearchCommand;
import cdri.infra.entity.BookJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository {
    List<BookJpaEntity> findSliceWithCategoryByCommandKeyset(BookSearchCommand command, int size);
    Optional<BookJpaEntity> findById(Long id);
}

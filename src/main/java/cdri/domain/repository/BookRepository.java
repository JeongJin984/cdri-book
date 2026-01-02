package cdri.domain.repository;

import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.infra.entity.BookJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository {
    List<BookSearchResult> findSliceWithCategoryByCommandKeyset(BookSearchCmd command, int size);
    Optional<BookJpaEntity> findById(Long id);
}

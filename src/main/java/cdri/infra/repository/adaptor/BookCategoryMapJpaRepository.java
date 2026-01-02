package cdri.infra.repository.adaptor;

import cdri.infra.entity.BookCategoryMapJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCategoryMapJpaRepository extends JpaRepository<BookCategoryMapJpaEntity, Long> {
}

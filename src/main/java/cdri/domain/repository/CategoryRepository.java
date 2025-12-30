package cdri.domain.repository;

import cdri.infra.entity.CategoryJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository {
    Optional<CategoryJpaEntity> findById(Long id);

}

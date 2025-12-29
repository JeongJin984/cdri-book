package cdri.infra.repository;

import cdri.domain.repository.CategoryRepository;
import cdri.infra.entity.CategoryJpaEntity;
import cdri.infra.repository.adaptor.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<CategoryJpaEntity> findByName(String id) {
        return categoryJpaRepository.findByName(id);
    }
}

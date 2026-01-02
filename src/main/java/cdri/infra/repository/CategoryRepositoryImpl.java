package cdri.infra.repository;

import cdri.domain.repository.CategoryRepository;
import cdri.infra.entity.CategoryJpaEntity;
import cdri.infra.repository.adaptor.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<CategoryJpaEntity> findById(Long id) {
        return categoryJpaRepository.findById(id);
    }

    @Override
    public List<CategoryJpaEntity> findAllByIds(List<Long> ids) {
        return categoryJpaRepository.findAllById(ids);
    }
}

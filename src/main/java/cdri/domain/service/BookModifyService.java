package cdri.domain.service;

import cdri.common.exception.NoSuchBookException;
import cdri.common.exception.NoSuchCategoryException;
import cdri.domain.dto.command.BookCategoryModifyCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.dto.result.CategoryModifyResult;
import cdri.domain.repository.BookRepository;
import cdri.domain.repository.CategoryRepository;
import cdri.infra.entity.BookJpaEntity;
import cdri.infra.entity.CategoryJpaEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookModifyService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public CategoryModifyResult modifyBookCategory(Long bookId, BookCategoryModifyCmd cmd) {
        BookJpaEntity book = bookRepository.findById(bookId)
            .orElseThrow(() -> new NoSuchBookException("No book found with id: " + bookId));

        List<CategoryJpaEntity> category = categoryRepository.findAllByIds(cmd.categoryId());

        if(category.size() != cmd.categoryId().size()) throw new NoSuchCategoryException("No category found with id: " + cmd.categoryId());

        book.changeCategory(category);

        return new CategoryModifyResult(
            book.getCategories().stream()
                .map(c -> new CategoryModifyResult.BookCategory(
                    c.getCategory().getCategoryId(),
                    c.getCategory().getName(),
                    c.getCategory().getCreatedAt()
                )).toList(),
            new CategoryModifyResult.Book(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getStatus(),
                book.getCreatedAt()
            )
        );
    }
}

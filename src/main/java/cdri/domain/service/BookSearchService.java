package cdri.domain.service;

import cdri.domain.dto.command.BookSearchCmd;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchService {
    private final BookRepository bookRepository;

    public List<BookSearchResult> searchBooks(BookSearchCmd command, int size) {
        return bookRepository.findSliceWithCategoryByCommandKeyset(command, size).stream()
            .map(v -> new BookSearchResult(
                new BookSearchResult.BookCategory(
                    v.getCategory().getCategoryId(),
                    v.getCategory().getName(),
                    v.getCategory().getCreatedAt()
                ),
                new BookSearchResult.Book(
                    v.getBookId(),
                    v.getTitle(),
                    v.getAuthor(),
                    v.getStatus(),
                    v.getCreatedAt()
                )
            )).toList();
    }
}

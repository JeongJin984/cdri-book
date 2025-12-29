package cdri.domain.service;

import cdri.domain.dto.command.BookSearchCommand;
import cdri.domain.dto.result.BookSearchResult;
import cdri.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookSearchService {
    private final BookRepository bookRepository;

    public List<BookSearchResult> searchBooks(BookSearchCommand command, int size) {
        return bookRepository.findSliceWithCategoryByCommandKeyset(command, size).stream()
            .map(v -> new BookSearchResult(
                v.getCategory().getName(),
                v.getBookId(),
                v.getTitle(),
                v.getAuthor(),
                v.getStatus(),
                v.getCreatedAt()
            )).toList();
    }
}

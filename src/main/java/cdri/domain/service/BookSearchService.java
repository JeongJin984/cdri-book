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
        return bookRepository.findSliceWithCategoryByCommandKeyset(command, size);
    }
}

package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.repositories.SachRepository;
import com.bookstore.book_sell_service.search.SachDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SachSearchService {

    private final SachRepository sachRepository;

    /**
     * Tìm kiếm sách (sử dụng MySQL fallback không cần Elasticsearch)
     */
    public List<SachDocument> searchFuzzy(String term) {
        String lowerTerm = term != null ? term.toLowerCase() : "";
        return sachRepository.findAll().stream()
                .filter(s -> s.getTenSach() != null && s.getTenSach().toLowerCase().contains(lowerTerm))
                .map(SachDocument::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý / Autocomplete
     */
    public List<SachDocument> searchAutocomplete(String term) {
        return searchFuzzy(term);
    }
}

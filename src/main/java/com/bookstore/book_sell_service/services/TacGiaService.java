package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.TacGiaRequest;
import com.bookstore.book_sell_service.dto.responses.TacGiaResponse;
import com.bookstore.book_sell_service.entity.TacGia;
import com.bookstore.book_sell_service.mapper.TacGiaMapper;
import com.bookstore.book_sell_service.repositories.TacGiaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TacGiaService {
    TacGiaRepository tacGiaRepository;
    TacGiaMapper tacGiaMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public TacGiaResponse createTacGia(TacGiaRequest request) {
        if (tacGiaRepository.existsByTenTG(request.getTenTG())) {
            throw new RuntimeException("Tác giả đã tồn tại");
        }
        TacGia tacGia = tacGiaMapper.toTacGia(request);
        return tacGiaMapper.toTacGiaResponse(tacGiaRepository.save(tacGia));
    }

    public List<TacGiaResponse> getAllTacGias() {
        return tacGiaRepository.findAll().stream()
                .map(tacGiaMapper::toTacGiaResponse)
                .collect(Collectors.toList());
    }

    public TacGiaResponse getTacGia(Long id) {
        return tacGiaMapper.toTacGiaResponse(tacGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả")));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public TacGiaResponse updateTacGia(Long id, TacGiaRequest request) {
        TacGia tacGia = tacGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả"));

        tacGiaMapper.updateTacGia(tacGia, request);
        return tacGiaMapper.toTacGiaResponse(tacGiaRepository.save(tacGia));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteTacGia(Long id) {
        if (!tacGiaRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tác giả");
        }
        tacGiaRepository.deleteById(id);
    }
}
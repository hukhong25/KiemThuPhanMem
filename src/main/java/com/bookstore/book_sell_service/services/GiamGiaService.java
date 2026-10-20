package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.GiamGiaRequest;
import com.bookstore.book_sell_service.dto.responses.GiamGiaResponse;
import com.bookstore.book_sell_service.entity.GiamGia;
import com.bookstore.book_sell_service.mapper.GiamGiaMapper;
import com.bookstore.book_sell_service.repositories.GiamGiaRepository;
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
public class GiamGiaService {
    GiamGiaRepository giamGiaRepository;
    GiamGiaMapper giamGiaMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public GiamGiaResponse createGiamGia(GiamGiaRequest request) {
        // Validate dữ liệu cơ bản
        if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc");
        }
        if (request.getChietKhau() < 0 || request.getChietKhau() > 100) {
            throw new RuntimeException("Chiết khấu phải từ 0% đến 100%");
        }

        GiamGia giamGia = giamGiaMapper.toGiamGia(request);
        return giamGiaMapper.toGiamGiaResponse(giamGiaRepository.save(giamGia));
    }

    public List<GiamGiaResponse> getAllGiamGias() {
        return giamGiaRepository.findAll().stream()
                .map(giamGiaMapper::toGiamGiaResponse)
                .collect(Collectors.toList());
    }

    public GiamGiaResponse getGiamGia(Long id) {
        return giamGiaMapper.toGiamGiaResponse(giamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá")));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public GiamGiaResponse updateGiamGia(Long id, GiamGiaRequest request) {
        GiamGia giamGia = giamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá"));

        // Validate lại khi update
        if (request.getNgayBatDau() != null && request.getNgayKetThuc() != null) {
            if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
                throw new RuntimeException("Ngày bắt đầu phải trước ngày kết thúc");
            }
        }

        giamGiaMapper.updateGiamGia(giamGia, request);
        return giamGiaMapper.toGiamGiaResponse(giamGiaRepository.save(giamGia));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteGiamGia(Long id) {
        if (!giamGiaRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy mã giảm giá");
        }
        giamGiaRepository.deleteById(id);
    }
}
package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.LoaiSach.LoaiSachRequest;
import com.bookstore.book_sell_service.entity.LoaiSach;
import com.bookstore.book_sell_service.mapper.LoaiSachMapper;
import com.bookstore.book_sell_service.repositories.TheLoaiRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoaiSachService {
    TheLoaiRepository theLoaiRepository;
    LoaiSachMapper loaiSachMapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public LoaiSach createLoaiSach(LoaiSachRequest request) {
        LoaiSach loaiSach = loaiSachMapper.toLoaiSach(request);
        return theLoaiRepository.save(loaiSach);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<LoaiSach> getAllLoaiSach() {
        return theLoaiRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public LoaiSach getLoaiSachById(Long id) {
        return theLoaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loại sách không tồn tại"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public LoaiSach updateLoaiSach(Long id, LoaiSachRequest request) {
        LoaiSach loaiSach = getLoaiSachById(id);
        loaiSachMapper.updateLoaiSach(loaiSach, request);
        return theLoaiRepository.save(loaiSach);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteLoaiSach(Long id) {
        theLoaiRepository.deleteById(id);
    }
}
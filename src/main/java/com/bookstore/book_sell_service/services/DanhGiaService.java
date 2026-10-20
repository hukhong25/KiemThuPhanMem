package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.DanhGia.DanhGiaRequest;
import com.bookstore.book_sell_service.dto.responses.DanhGiaResponse;
import com.bookstore.book_sell_service.entity.DanhGia;
import com.bookstore.book_sell_service.entity.KhachHang;
import com.bookstore.book_sell_service.entity.Sach;
import com.bookstore.book_sell_service.exception.AppException;
import com.bookstore.book_sell_service.exception.ErrorCode;
import com.bookstore.book_sell_service.repositories.DanhGiaRepository;
import com.bookstore.book_sell_service.repositories.KhachHangRepository;
import com.bookstore.book_sell_service.repositories.SachRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DanhGiaService {

    KhachHangRepository khachHangRepository;
    DanhGiaRepository danhGiaRepository;
    SachRepository sachRepository;
    AuthenticationService authenticationService;
    @PreAuthorize("isAuthenticated()")
    public DanhGia createDanhGia(DanhGiaRequest danhGiaRequest){

        KhachHang khachHang = authenticationService.khachHang();


        Sach sach = sachRepository.findById(danhGiaRequest.getMaSach())
                .orElseThrow(() -> new RuntimeException("Khong tim thay sach"));

        DanhGia danhGia = new DanhGia();
        danhGia.setSoSao(danhGiaRequest.getSoSao());
        danhGia.setBinhLuan(danhGiaRequest.getBinhLuan());
        danhGia.setNgayBL(LocalDateTime.now());
        danhGia.setKhachHang(khachHang);
        danhGia.setSach(sach);

        return danhGiaRepository.save(danhGia);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<DanhGiaResponse> getAllDanhGia(){
       return danhGiaRepository.findALLDanhGia();
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<DanhGiaResponse> getAllByMaSach(Long maSach){
        return danhGiaRepository.findALLDanhGiaByMaSach(maSach);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteBL(Long maDanhGia)
    {
        danhGiaRepository.deleteById(maDanhGia);
    }

}

package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.responses.ThongKeResponse;
import com.bookstore.book_sell_service.repositories.ChiTietDHRepository;
import com.bookstore.book_sell_service.repositories.DonHangRepository;
import com.bookstore.book_sell_service.repositories.SachRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DoanhThu_ThongKe_BaoCaoAService {

    ChiTietDHRepository chiTietDHRepository;
    DonHangRepository donHangRepository;
    SachRepository sachRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ThongKeResponse thongKeResponse (){

        Pageable top1 = PageRequest.of(0,1);
        return ThongKeResponse.builder()
                .tongDoanhThu(chiTietDHRepository.tongDoanhThu())
                .tongOonHang(donHangRepository.tongDonHang())
                .tongKhachHang(donHangRepository.tongKhachHang())
                .sachBanMax(chiTietDHRepository.sachBanChay(top1).get(0))
                .tongSachBan(chiTietDHRepository.tongSoSachBan())
                .danhSach(chiTietDHRepository.thongkeList())
                .thongKeTKT(sachRepository.thongKeTKT())
                .build();
    }
}

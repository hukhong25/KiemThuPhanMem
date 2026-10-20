package com.bookstore.book_sell_service.services;

import com.bookstore.book_sell_service.dto.request.GioHang.GioHangDelete;
import com.bookstore.book_sell_service.dto.request.GioHang.GioHangRequest;
import com.bookstore.book_sell_service.dto.request.GioHang.GioHangUpdate;
import com.bookstore.book_sell_service.dto.responses.ChiTietGHResponse;
import com.bookstore.book_sell_service.dto.responses.GioHangResponse;
import com.bookstore.book_sell_service.dto.responses.Sach_in_GioHang;
import com.bookstore.book_sell_service.entity.*;
import com.bookstore.book_sell_service.exception.AppException;
import com.bookstore.book_sell_service.exception.ErrorCode;
import com.bookstore.book_sell_service.repositories.ChiTietGioHangRepository;
import com.bookstore.book_sell_service.repositories.GioHangRepository;
import com.bookstore.book_sell_service.repositories.KhachHangRepository;
import com.bookstore.book_sell_service.repositories.SachRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GioHangService {

    GioHangRepository gioHangRepository;
    ChiTietGioHangRepository chiTietGioHangRepository;
    KhachHangRepository khachHangRepository;
    SachRepository sachRepository;
    AuthenticationService authenticationService;


    @Transactional
    public void addSachGioHang(GioHangRequest request) {
        // Lấy thông tin user

        KhachHang user = authenticationService.khachHang();

        // Lấy sách
        Sach sach = sachRepository.findById(request.getMaSach())
                .orElseThrow(() -> new RuntimeException("Loi ko tim thay sach"));

        // Tìm hoặc tạo giỏ hàng
        GioHang gioHang = gioHangRepository.findByKhachHang_UserName(user.getUserName())
                .orElseGet(() -> {
                    GioHang newGioHang = new GioHang();
                    newGioHang.setKhachHang(user);
                    newGioHang.setChiTietGioHangList(new ArrayList<>());
                    return gioHangRepository.saveAndFlush(newGioHang);
                });

        // ✅ Kiểm tra sách đã tồn tại bằng method custom
        Optional<ChiTietGioHang> chiTietOptional =
                chiTietGioHangRepository.findByGioHangAndSach(gioHang, sach);

        if (chiTietOptional.isEmpty()) {
            //  Tạo ID trước
            ChiTietGioHangId chiTietId = new ChiTietGioHangId(
                    gioHang.getMaGioHang(),
                    sach.getMaSach()
            );

            //  Tạo chi tiết mới VÀ SET ID

            ChiTietGioHang newChiTiet = ChiTietGioHang.builder()
                    .id(chiTietId)  // ← QUAN TRỌNG: Phải set ID
                    .gioHang(gioHang)
                    .sach(sach)
                    .soLuongMua(
                            ((request.getSoLuong() == null) ? 1 : request.getSoLuong())
                    )
                    .build();

            chiTietGioHangRepository.save(newChiTiet);
        } else {
            ChiTietGioHang chiTietGioHang = chiTietOptional.get();
            chiTietGioHang.setSoLuongMua(chiTietGioHang.getSoLuongMua() + 1);
        }
         gioHangRepository.findById(gioHang.getMaGioHang())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tìm thấy"));
    }

    @Transactional
    public void updateSoLuong (GioHangUpdate gioHangUpdate){

        KhachHang user = authenticationService.khachHang();


        // Lấy sách
        Sach sach = sachRepository.findById(gioHangUpdate.getMaSach())
                .orElseThrow(() -> new RuntimeException("Loi ko tim thay sach"));

        GioHang gioHang = gioHangRepository.findByKhachHang_UserName(user.getUserName())
                .orElseThrow(() -> new RuntimeException("Ko tim thay gio hang"));

        ChiTietGioHang chiTietGioHang = chiTietGioHangRepository.findByGioHangAndSach(gioHang,sach)
                .orElseThrow(() -> new RuntimeException("Ko tim thay "));

        if (gioHangUpdate.getAction().equalsIgnoreCase("INCREASE")){
            chiTietGioHang.setSoLuongMua(chiTietGioHang.getSoLuongMua() + 1);
        }
        else {
            Integer soLuong = chiTietGioHang.getSoLuongMua();
            if (soLuong > 1){
                chiTietGioHang.setSoLuongMua(chiTietGioHang.getSoLuongMua() - 1);
                chiTietGioHangRepository.save(chiTietGioHang);

            }
            else {
                chiTietGioHangRepository.deleteByGioHangAndSach(gioHang,sach);
            }

        }
    }
    

    @Transactional
    public void deleteSach(Long maSach) {

        KhachHang user = authenticationService.khachHang();

        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new RuntimeException("Loi ko tim thay sach"));

        GioHang gioHang = gioHangRepository.findByKhachHang_UserName(user.getUserName())
                .orElseThrow(() -> new RuntimeException("Ko tim thay gio hang"));

        ChiTietGioHang chiTietGioHang = chiTietGioHangRepository.findByGioHangAndSach(gioHang, sach)
                .orElseThrow(() -> new RuntimeException("Ko tim thay "));

        chiTietGioHangRepository.deleteByGioHangAndSach(gioHang, sach);
    }

    // xem gio hang cua minh
    public GioHangResponse getGHOfMaKH (){

        KhachHang khachHang = authenticationService.khachHang();

        GioHang gioHang = gioHangRepository.findByKhachHang_UserName(khachHang.getUserName())
                .orElseThrow(() -> new RuntimeException("Khong tim thay gio hang"));


        List<ChiTietGioHang> chiTietGioHangList = chiTietGioHangRepository.findAllByGioHang(gioHang);

        if (chiTietGioHangList.isEmpty()){
            throw new RuntimeException("Gio hang ko co san pham");
        }

        List <ChiTietGHResponse> responses =
                chiTietGioHangList.stream()
                        .map( ct -> {
                            Sach sach = ct.getSach();
                            Sach_in_GioHang sachInGioHang = new Sach_in_GioHang(
                                    sach.getMaSach(),
                                    sach.getTenSach(),
                                    sach.getDonGia()
                            );
                            return new ChiTietGHResponse(
                                    sachInGioHang,
                                    sach.getDonGia(),
                                    ct.getSoLuongMua(),
                                    sach.getDonGia() * ct.getSoLuongMua()
                            );
                            })
                        .collect(Collectors.toList());
        double tongTien = responses.stream()
                .mapToDouble(ChiTietGHResponse::getThanhTien)
                .sum();
        return GioHangResponse.builder()
                .maGioHang(gioHang.getMaGioHang())
                .chiTietGHResponses(responses)
                .tongTien(tongTien)
                .build();
    }

}
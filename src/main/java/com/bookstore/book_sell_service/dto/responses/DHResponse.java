package com.bookstore.book_sell_service.dto.responses;


import com.bookstore.book_sell_service.entity.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DHResponse {

        private Long maDonHang;
        private String tenNguoiNhan;
        private String soDTNguoiNhan;
        private String email;
        private String diaChiGiaoHang;
        private String ghiChu;
        private Double phiGiaoHang;
        private String trangThai;
        private LocalDate ngayDat;
        private double tongTien;

        private QuanHuyen quanHuyen;

        @JsonIgnoreProperties("donHangList") // Bỏ qua donHangList khi serialize
        private PhuongThucThanhToan phuongThucThanhToan;

        @JsonIgnoreProperties("donHangList")
        private NhanVien nhanVien;

        private GiamGia giamGia;

//        @JsonIgnoreProperties("donHang") // Bỏ qua tham chiếu ngược về đơn hàng
//        private List<DonHangChiTiet> chiTietDonHangList;
}


package com.bookstore.book_sell_service.dto.request.DonHang;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonHangCreate {

     String tenNguoiNhan;
     String email;
     String diaChiGiaoHang;
     Double phiGiaoHang;
     String trangThai;
     LocalDate ngayDat;
     String soDTNguoiNhan;
     Long maGiamGia;
     Long maPTTT;
     Long maQuanHuyen;
}

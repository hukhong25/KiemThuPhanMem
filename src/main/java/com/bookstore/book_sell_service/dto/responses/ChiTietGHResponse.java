package com.bookstore.book_sell_service.dto.responses;

import com.bookstore.book_sell_service.entity.Sach;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChiTietGHResponse {
    Sach_in_GioHang sach;
    Double giaTien;
    Integer soLuong;
    Double thanhTien;


}

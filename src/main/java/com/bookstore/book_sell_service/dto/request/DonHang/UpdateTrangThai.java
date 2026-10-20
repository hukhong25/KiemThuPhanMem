package com.bookstore.book_sell_service.dto.request.DonHang;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTrangThai {

    Long maDonHang;
    String trangThai;

}

package com.bookstore.book_sell_service.dto.responses;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThongKeResponse {

    Integer tongSachBan;
    Double tongDoanhThu;
    List<ThongKeSach> danhSach;
    List<ThongKeSach> thongKeTKT;
    Long tongOonHang;
    Long tongKhachHang;
    String sachBanMax;
}

package com.bookstore.book_sell_service.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeSach {
    private String tenSach;
    private Long soLuongMua;
    private Double tongTien;
    private Integer soLuongCo;

    // Constructor cho query thongKeTKT
    public ThongKeSach(String tenSach, Integer soLuongCo) {
        this.tenSach = tenSach;
        this.soLuongCo = soLuongCo;
    }

    // Constructor cho các query khác
    public ThongKeSach(String tenSach, Long soLuongMua, Double tongTien) {
        this.tenSach = tenSach;
        this.soLuongMua = soLuongMua;
        this.tongTien = tongTien;
    }
}

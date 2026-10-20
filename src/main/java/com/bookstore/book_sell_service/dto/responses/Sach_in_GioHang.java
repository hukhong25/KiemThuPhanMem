package com.bookstore.book_sell_service.dto.responses;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sach_in_GioHang {
    private Long maSach;
    private String tenSach;
    private Double donGia;

}

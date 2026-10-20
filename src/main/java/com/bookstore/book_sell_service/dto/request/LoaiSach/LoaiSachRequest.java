package com.bookstore.book_sell_service.dto.request.LoaiSach;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaiSachRequest {
    private String tenLoai;
    private String moTa;
}
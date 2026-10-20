package com.bookstore.book_sell_service.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuanHuyenRequest {

    private String tenQuanHuyen;
    private Long maTinh;
}

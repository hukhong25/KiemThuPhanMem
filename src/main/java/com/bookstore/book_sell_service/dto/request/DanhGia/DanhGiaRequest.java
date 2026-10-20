package com.bookstore.book_sell_service.dto.request.DanhGia;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DanhGiaRequest {


//    private String maKH;
    private Long maSach;
    private Integer soSao;
    private String binhLuan;
}

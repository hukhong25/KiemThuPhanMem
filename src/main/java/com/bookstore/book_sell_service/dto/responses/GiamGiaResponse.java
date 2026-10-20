package com.bookstore.book_sell_service.dto.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiamGiaResponse {
    Long maGiamGia;
    LocalDate ngayBatDau;
    LocalDate ngayKetThuc;
    Double chietKhau;
    Double donHangToiThieu;
    String moTa;
}
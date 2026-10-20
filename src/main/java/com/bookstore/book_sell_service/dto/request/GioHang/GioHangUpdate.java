package com.bookstore.book_sell_service.dto.request.GioHang;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GioHangUpdate {
    Long maSach;
    // action INCREASE → tăng số lượng
//     action = DECREASE → giảm số lượng
    String action;
}

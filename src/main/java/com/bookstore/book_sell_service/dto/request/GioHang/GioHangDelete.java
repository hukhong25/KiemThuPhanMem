package com.bookstore.book_sell_service.dto.request.GioHang;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GioHangDelete {
    Long maSach;
}

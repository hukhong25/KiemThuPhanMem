package com.bookstore.book_sell_service.dto.request.Sach;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SachFilterRequest {

    private Double minPrice;
    private Double maxPrice;
    private String orderBy;
    private String order;
}

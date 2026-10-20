package com.bookstore.book_sell_service.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TinhRequest {
    private String tenTinh;
}
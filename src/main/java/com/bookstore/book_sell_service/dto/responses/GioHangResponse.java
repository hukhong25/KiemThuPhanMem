package com.bookstore.book_sell_service.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GioHangResponse {

    Long maGioHang;
    List<ChiTietGHResponse> chiTietGHResponses;
    double tongTien;


}

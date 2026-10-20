package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.DonHang.DonHangCreate;
import com.bookstore.book_sell_service.dto.responses.DHResponse;
import com.bookstore.book_sell_service.entity.DonHang;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DonHangMapper {
    DonHang toDonHang (DonHangCreate donHangCreate);
    List<DHResponse> togetAllDH (List<DonHang> donHang);
    DHResponse togetDH(DonHang donHang);
}

package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.GiamGiaRequest;
import com.bookstore.book_sell_service.dto.responses.GiamGiaResponse;
import com.bookstore.book_sell_service.entity.GiamGia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface GiamGiaMapper {
    GiamGia toGiamGia(GiamGiaRequest request);
    GiamGiaResponse toGiamGiaResponse(GiamGia giamGia);
    void updateGiamGia(@MappingTarget GiamGia giamGia, GiamGiaRequest request);
}
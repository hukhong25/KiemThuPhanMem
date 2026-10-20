package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.TacGiaRequest;
import com.bookstore.book_sell_service.dto.responses.TacGiaResponse;
import com.bookstore.book_sell_service.entity.TacGia;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TacGiaMapper {
    TacGia toTacGia(TacGiaRequest request);
    TacGiaResponse toTacGiaResponse(TacGia tacGia);
    void updateTacGia(@MappingTarget TacGia tacGia, TacGiaRequest request);
}
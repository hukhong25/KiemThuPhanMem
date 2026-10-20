package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.TinhRequest;
import com.bookstore.book_sell_service.entity.Tinh;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TinhMapper {
    Tinh toTinh(TinhRequest request);
}

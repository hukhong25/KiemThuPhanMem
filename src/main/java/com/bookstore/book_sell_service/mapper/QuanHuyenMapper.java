package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.QuanHuyenRequest;
import com.bookstore.book_sell_service.entity.QuanHuyen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuanHuyenMapper {
    QuanHuyen toQuanHuyen(QuanHuyenRequest request);
}

package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.LoaiSach.LoaiSachRequest;
import com.bookstore.book_sell_service.entity.LoaiSach;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LoaiSachMapper {
    LoaiSach toLoaiSach(LoaiSachRequest request);
    void updateLoaiSach(@MappingTarget LoaiSach loaiSach, LoaiSachRequest request);
}
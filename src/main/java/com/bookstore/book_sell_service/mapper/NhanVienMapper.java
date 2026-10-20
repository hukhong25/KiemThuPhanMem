package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.NhanVien.NhanVienCreationalRequest;
import com.bookstore.book_sell_service.dto.request.NhanVien.NhanVienUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.NVResponse;
import com.bookstore.book_sell_service.entity.NhanVien;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface NhanVienMapper {
    NhanVien toNhanVien(NhanVienCreationalRequest request);
    void updateNhanVien(@MappingTarget NhanVien user, NhanVienUpdateRequest request);
    NVResponse toNhanVienResponse(NhanVien nhanVien);
}

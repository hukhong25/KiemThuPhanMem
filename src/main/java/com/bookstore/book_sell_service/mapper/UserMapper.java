package com.bookstore.book_sell_service.mapper;

import com.bookstore.book_sell_service.dto.request.KhachHang.KhachHangCreationRequest;
import com.bookstore.book_sell_service.dto.request.KhachHang.KhachHangUpdateRequest;
import com.bookstore.book_sell_service.dto.responses.KHResponse;
import com.bookstore.book_sell_service.entity.KhachHang;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {


    KhachHang toUser(KhachHangCreationRequest request);

    void updateKH(@MappingTarget KhachHang user, KhachHangUpdateRequest request);

    KHResponse toKHResponse(KhachHang khachHang);
}

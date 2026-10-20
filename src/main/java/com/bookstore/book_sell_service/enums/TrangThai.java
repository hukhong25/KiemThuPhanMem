package com.bookstore.book_sell_service.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum TrangThai {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DA_XAC_NHAN("Đã xác nhận"),
    DANG_CHUAN_BI("Đang chuẩn bị"),
    DANG_GIAO("Đang giao"),
    DA_GIAO("Đã giao"),
    DA_HUY("Đã hủy"),
    TRA_HANG("Trả hàng");

    private final String moTa;

    TrangThai(String moTa) {
        this.moTa = moTa;
    }

    @JsonValue
    public String getMoTa() {
        return moTa;
    }




}

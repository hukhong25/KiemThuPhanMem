package com.bookstore.book_sell_service.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NVResponse {
    private String maNV;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String soCCCD;
    private Set<String> roles;

}

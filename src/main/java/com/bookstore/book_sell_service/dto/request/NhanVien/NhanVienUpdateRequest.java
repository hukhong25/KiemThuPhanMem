package com.bookstore.book_sell_service.dto.request.NhanVien;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NhanVienUpdateRequest {
    private String tenDangNhap;
    private String matKhau;
}

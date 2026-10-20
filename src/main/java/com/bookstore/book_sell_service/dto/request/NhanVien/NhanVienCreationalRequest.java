package com.bookstore.book_sell_service.dto.request.NhanVien;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NhanVienCreationalRequest {
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String soCCCD;
}

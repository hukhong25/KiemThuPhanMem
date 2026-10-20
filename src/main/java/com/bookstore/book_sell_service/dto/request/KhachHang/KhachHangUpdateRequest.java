package com.bookstore.book_sell_service.dto.request.KhachHang;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KhachHangUpdateRequest {

    private String hoTen;

    @Email(message = "INVALID_EMAIL")
    private String email;
    @Column(nullable = false, unique = true)
    private String soDT;
    private String diaChi;

    @NotNull(message = "maQuanHuyen is required")
    private Long maQuanHuyen;
}

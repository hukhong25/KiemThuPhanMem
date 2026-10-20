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
public class KhachHangUpdatePassWordRequest {

    private String matKhau;
    @Size(min=8, message = "INVALID_PASSWORD")
    @NotBlank(message = "BLANK_PASSWORD")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "INVALID_PASSWORD"
    )
    private String newPassWord;
    private String verifynewPassWord;



}

package com.bookstore.book_sell_service.dto.request.KhachHang;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KhachHangCreationRequest {

    private String hoTen;
    @Column(nullable = false, unique = true)
    @Size(min=3,message = "INVALID_USERNAME")
    @NotBlank(message = "INVALID_USERNAME" )
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "INVALID_USERNAME"
    )
    private String userName;
    @Size(min=8,message = "INVALID_PASSWORD")
    @NotBlank(message = "BLANK_PASSWORD")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "INVALID_PASSWORD"
    )
    private String matKhau;
    @Email(message = "INVALID_EMAIL")
    private String email;
    @Column(nullable = false, unique = true)
    private String soDT;
    private String diaChi;
    private Long maQuanHuyen;
}

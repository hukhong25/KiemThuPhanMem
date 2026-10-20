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
public class KHResponse {
    private String maKH;
    private String hoTen;
    private String userName;
    private String email;
    private String soDT;
    private String diaChi;
    private String tenQuanHuyen;
    private String tenTinh;
    private Set<String> roles;
    }

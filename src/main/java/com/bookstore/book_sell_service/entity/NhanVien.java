package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "NHAN_VIEN")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maNV;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String soCCCD;
    private Set<String> roles;

    @OneToMany(mappedBy = "nhanVien")
    @JsonIgnore
    private List<DonHang> donHangList;
}

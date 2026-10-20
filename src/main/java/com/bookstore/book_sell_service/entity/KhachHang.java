package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "KHACH_HANG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String maKH;
    private String hoTen;
    @Column(unique = true)
    private String userName;
    private String matKhau;
    private String email;
    @Column(unique = true)
    private String soDT;
    private String diaChi;
    private Set<String> roles;

    @ManyToOne
    @JoinColumn(name = "maQuanHuyen")
    private QuanHuyen quanHuyen;

//    cascade = CascadeType.ALL: các thao tác (lưu, xóa,...) trên KhachHang sẽ được áp dụng cho GioHang.
//    fetch = FetchType.LAZY: chỉ tải giỏ hàng khi được yêu cầu.
    @OneToOne(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "khachhang-giohang")

    private GioHang gioHang;

    @OneToMany(mappedBy = "khachHang")

    private List<DanhGia> danhGiaList;

    @OneToMany(mappedBy = "khachHang")
    @JsonIgnore
    private List<DonHang> donHangList;
}

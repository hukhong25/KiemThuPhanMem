package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GIO_HANG")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maGioHang;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH", referencedColumnName = "maKH")
    @JsonBackReference(value = "khachhang-giohang")

    private KhachHang khachHang;

    @OneToMany(
            mappedBy = "gioHang",
            cascade = CascadeType.ALL,  // ← Thêm cascade
            orphanRemoval = true,        // ← Thêm orphanRemoval
            fetch = FetchType.LAZY       // ← Thêm lazy fetch
    )
    @Builder.Default
    private List<ChiTietGioHang> chiTietGioHangList = new ArrayList<>();

}

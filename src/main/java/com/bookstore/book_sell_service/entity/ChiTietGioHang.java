package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_gio_hang")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietGioHang {
    @EmbeddedId
    private ChiTietGioHangId id;

    private Integer soLuongMua;

    @ManyToOne
    @MapsId("maGioHang")
    @JoinColumn(name = "ma_gio_hang")
    private GioHang gioHang;

    @ManyToOne
    @MapsId("maSach")
    @JoinColumn(name = "ma_sach")

    private Sach sach;
    }


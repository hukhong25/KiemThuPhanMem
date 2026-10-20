package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DON_HANG_CHI_TIET")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonHangChiTiet {
    @EmbeddedId
    private DonHangChiTietId id;

    private Integer soLuongMua;
    private Double giaMua;

    @ManyToOne
    @MapsId("maDonHang")
    @JoinColumn(name = "ma_don_hang")
    @JsonBackReference(value = "donhang-chitiet")
    private DonHang donHang;

    @ManyToOne
    @MapsId("maSach")
    @JoinColumn(name = "ma_sach")
    @JsonBackReference(value = "sach-chitiet")
    private Sach sach;


}

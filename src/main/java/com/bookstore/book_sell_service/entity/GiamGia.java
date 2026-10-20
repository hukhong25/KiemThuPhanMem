package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "GIAM_GIA")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maGiamGia;

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Double chietKhau;
    private String moTa;

    Double donHangToiThieu;
    @OneToMany(mappedBy = "giamGia")
    @JsonIgnore
    private List<DonHang> donHangList;

    @OneToMany(mappedBy = "giamGia")
    private List<SachGiamGia> sachGiamGiaList;

}

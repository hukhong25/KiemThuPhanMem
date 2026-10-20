package com.bookstore.book_sell_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SACH_GIAM_GIA")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SachGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer soLuongApDungGiamGia;

    @ManyToOne
    @JoinColumn(name = "maSach")
    private Sach sach;

    @ManyToOne
    @JoinColumn(name = "maGiamGia")
    private GiamGia giamGia;
}

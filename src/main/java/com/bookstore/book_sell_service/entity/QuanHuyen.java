package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "QUAN_HUYEN")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuanHuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maQuanHuyen;
    private String tenQuanHuyen;

    @ManyToOne(optional= true)
    @JoinColumn(name = "maTinh")
    private Tinh tinh;

    @OneToMany(mappedBy = "quanHuyen")
    @JsonIgnore
    private List<KhachHang> khachHangList;

    @OneToMany(mappedBy = "quanHuyen")
    @JsonIgnore
    private List<DonHang> donHangList;
}

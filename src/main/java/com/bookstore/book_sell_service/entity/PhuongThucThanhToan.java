package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "PHUONG_THUC_THANH_TOAN")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhuongThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPTTT;

    private String tenPTTT;


    @OneToMany(mappedBy = "phuongThucThanhToan")
    @JsonIgnore
    private List<DonHang> donHangList;
}

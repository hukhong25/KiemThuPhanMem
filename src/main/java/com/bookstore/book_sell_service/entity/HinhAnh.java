package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HINH_ANH" )
public class HinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maHinhAnh")
    private Long maHinhAnh; // Mã hình ảnh (PK)

    private String maAnh;
    // FK: MaSP (Giả định MaSP là Mã Sách, Many-to-One với Entity Book)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maSach", nullable = false)
    private Sach sach;
}

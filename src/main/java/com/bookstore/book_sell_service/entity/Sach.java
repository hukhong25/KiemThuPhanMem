package com.bookstore.book_sell_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "SACH")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSach;
    private String tenSach;
    private Double donGia;
    private String donViTinh;
    private Integer soLuongCo;
    private String khoHang;
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "maLoai")
    private LoaiSach loaiSach;

    @ManyToOne
    @JoinColumn(name = "maNXB")
    private NhaXuatBan nhaXuatBan;

    @OneToMany(mappedBy = "sach")
    private List<HinhAnh> hinhAnhList;

    @OneToMany(mappedBy = "sach")
    @JsonManagedReference(value = "sach-chitietGH")
    private List<ChiTietGioHang> chiTietGioHangList;

    @OneToMany(mappedBy = "sach")
    @JsonManagedReference(value = "sach-chitiet")
    private List<DonHangChiTiet> donHangChiTietList;

    @ManyToMany
    @JoinTable(
            name = "SACH_TAC_GIA",
            joinColumns = @JoinColumn(name = "maSach"),
            inverseJoinColumns = @JoinColumn(name = "maTG")
    )
    private Set<TacGia> tacGiaSet;

    @OneToMany
    private List<SachGiamGia> sachGiamGiaList;
}

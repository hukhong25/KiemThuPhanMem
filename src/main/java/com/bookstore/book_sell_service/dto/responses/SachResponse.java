package com.bookstore.book_sell_service.dto.responses;

import com.bookstore.book_sell_service.entity.HinhAnh;
import com.bookstore.book_sell_service.entity.LoaiSach;
import com.bookstore.book_sell_service.entity.NhaXuatBan;
import com.bookstore.book_sell_service.entity.TacGia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SachResponse {
    private Long maSach;
    private String tenSach;
    private Double donGia;
    private String donViTinh;
    private Integer soLuongCo;
    private String khoHang;
    private String moTa;
    private LoaiSach loaiSach;
    private NhaXuatBan nhaXuatBan;
    private Set<TacGia> tacGiaSet;
    private List<String> hinhAnhs;
    private Long soLuotDG;
    private Float avgSao;

}

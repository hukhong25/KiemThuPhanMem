package com.bookstore.book_sell_service.dto.request.Sach;

import com.bookstore.book_sell_service.entity.HinhAnh;
import com.bookstore.book_sell_service.entity.LoaiSach;
import com.bookstore.book_sell_service.entity.NhaXuatBan;
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
public class SachUpdateRequest {
//    private Long maSach;
    private String tenSach;
    private Double donGia;
    private String donViTinh;
    private Integer soLuongCo;
    private String khoHang;
    private String moTa;
    private LoaiSach loaiSach;
    private NhaXuatBan nhaXuatBan;
    private List<HinhAnh> hinhAnhList;
    private Set<Long> tacGiaIds;
}

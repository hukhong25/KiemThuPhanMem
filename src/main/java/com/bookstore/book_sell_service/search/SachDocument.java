package com.bookstore.book_sell_service.search;

import com.bookstore.book_sell_service.entity.Sach;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "sach_index") // Tên của index trên Elasticsearch
@Setting(settingPath = "elasticsearch-analyzer.json") // Áp dụng file Analyzer đã tạo
public class SachDocument {
    @Id
    @Field(type = FieldType.Keyword,
            analyzer = "autocomplete_analyzer", // Dùng analyzer tùy chỉnh
            searchAnalyzer = "standard") // Khi tìm, dùng analyzer tiêu chuẩn
    private Long maSach;

    @Field(type = FieldType.Text,
            analyzer = "autocomplete_analyzer", // Dùng analyzer tùy chỉnh
            searchAnalyzer = "standard") // Khi tìm, dùng analyzer tiêu chuẩn
    private String tenSach;

    private String moTa;

    @Field(type = FieldType.Double)
    private Double donGia;

    @Field(type = FieldType.Keyword) // Dùng Keyword cho các trường lọc chính xác
    private String donViTinh;

    @Field(type = FieldType.Integer)
    private Integer soLuongCo;

    // --- Hàm chuyển đổi từ JPA Entity (Sach) sang Document (SachDocument) ---
    public static SachDocument fromEntity(Sach sach) {
        return SachDocument.builder()
                .maSach(sach.getMaSach())
                .tenSach(sach.getTenSach())
                .moTa(sach.getMoTa())
                .donGia(sach.getDonGia())
                .donViTinh(sach.getDonViTinh())
                .soLuongCo(sach.getSoLuongCo())
                .build();
    }
}

package com.bookstore.book_sell_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
public class ChiTietGioHangId implements Serializable {
    private Long maGioHang;

    private Long maSach;

    public ChiTietGioHangId(){

    }
    public ChiTietGioHangId(Long maGioHang, Long maSach) {
        this.maGioHang=maGioHang;
        this.maSach=maSach;
    }


    // getters and setters, hashCode, equals
    public Long getMaGioHang() {
        return maGioHang;
    }
    public void setMaGioHang(Long maGioHang) {
        this.maGioHang = maGioHang;
    }
    public Long getMaSach() {
        return maSach;
    }
    public void setMaSach(Long maSach) {
        this.maSach = maSach;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;  // ← Xử lý null

        ChiTietGioHangId that = (ChiTietGioHangId) o;
        return Objects.equals(maGioHang, that.maGioHang) &&
                Objects.equals(maSach, that.maSach);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maGioHang, maSach);
    }
}

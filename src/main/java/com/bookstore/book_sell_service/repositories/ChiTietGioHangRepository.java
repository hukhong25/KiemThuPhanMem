package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.ChiTietGioHang;
import com.bookstore.book_sell_service.entity.ChiTietGioHangId;
import com.bookstore.book_sell_service.entity.GioHang;
import com.bookstore.book_sell_service.entity.Sach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, ChiTietGioHangId> {
    Optional<ChiTietGioHang> findByGioHangAndSach(GioHang gioHang, Sach sach);

    void deleteByGioHangAndSach(GioHang gioHang, Sach sach);


    void deleteByGioHang(GioHang gioHang);

    void deleteByGioHang_MaGioHang(Long maGioHang);

    List<ChiTietGioHang> findAllByGioHang(GioHang gioHang);

}
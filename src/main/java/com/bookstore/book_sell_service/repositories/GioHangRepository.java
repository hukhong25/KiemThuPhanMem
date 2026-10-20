package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.DonHang;
import com.bookstore.book_sell_service.entity.GioHang;
import com.bookstore.book_sell_service.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang,Long> {
    Optional<GioHang> findByKhachHang_maKH(Optional<KhachHang> khachHang);
    Optional<GioHang> findByKhachHang_UserName (String userName);

    void deleteByKhachHang(KhachHang khachHang);

    void deleteByMaGioHang(Long maGioHang);
}

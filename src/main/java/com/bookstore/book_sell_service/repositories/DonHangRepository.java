package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang,Long> {

    @Query("SELECT COUNT(ct.maDonHang) FROM DonHang ct")
    Long tongDonHang();

    @Query("SELECT COUNT(DISTINCT ct.khachHang.maKH) FROM DonHang ct")
    Long tongKhachHang();

    List<DonHang> findAllByKhachHang_maKH(String maKH);
}

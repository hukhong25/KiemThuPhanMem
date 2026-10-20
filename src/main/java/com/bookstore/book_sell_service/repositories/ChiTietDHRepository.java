package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.dto.responses.ThongKeSach;
import com.bookstore.book_sell_service.entity.DonHangChiTiet;
import com.bookstore.book_sell_service.entity.DonHangChiTietId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Repository
public interface ChiTietDHRepository extends JpaRepository<DonHangChiTiet, DonHangChiTietId > {

    @Query("SELECT SUM(ct.soLuongMua) FROM DonHangChiTiet ct")
    Integer tongSoSachBan();

    @Query("SELECT ct.sach.tenSach " +
            "FROM DonHangChiTiet ct " +
            "GROUP BY ct.sach.tenSach " +
            "ORDER BY SUM(ct.soLuongMua) DESC")
    List<String> sachBanChay(Pageable pageable);

    @Query("SELECT SUM(ct.giaMua) FROM DonHangChiTiet ct")
    Double tongDoanhThu();

    @Query("SELECT new com.bookstore.book_sell_service.dto.responses.ThongKeSach(" +
            "ct.sach.tenSach, " +
            "SUM(ct.soLuongMua), " +
            "SUM(ct.giaMua)) " +
            "FROM DonHangChiTiet ct " +
            "GROUP BY ct.sach.tenSach")
    List<ThongKeSach> thongkeList();

}

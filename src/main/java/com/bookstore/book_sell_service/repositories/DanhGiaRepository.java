package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.dto.responses.DanhGiaResponse;
import com.bookstore.book_sell_service.entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia,Long> {

    @Query(
            "SELECT new com.bookstore.book_sell_service.dto.responses.DanhGiaResponse(" +
                    "n.hoTen, s.tenSach, bl.soSao, bl.binhLuan, bl.ngayBL, bl.maDanhGia) " +
                    "FROM DanhGia bl " +
                    "JOIN bl.sach s " +
                    "JOIN bl.khachHang n"
    )
    List<DanhGiaResponse> findALLDanhGia();

    @Query(
            "SELECT new com.bookstore.book_sell_service.dto.responses.DanhGiaResponse(" +
                    "n.hoTen, s.tenSach, bl.soSao, bl.binhLuan, bl.ngayBL, bl.maDanhGia) " +
                    "FROM DanhGia bl " +
                    "JOIN bl.sach s " +
                    "JOIN bl.khachHang n " +
                    "WHERE s.maSach = :maSach"
    )
    List<DanhGiaResponse> findALLDanhGiaByMaSach(@Param("maSach") Long maSach);

    @Query("SELECT COUNT(bl.maDanhGia) FROM DanhGia bl  WHERE bl.sach.maSach = :maSach")
    Long tongSoDanhGiaOfSach (@Param("maSach") Long maSach);

    @Query("SELECT SUM(bl.soSao) FROM DanhGia bl WHERE bl.sach.maSach = :maSach")
    Long tongSoSaoOfSach (@Param("maSach") Long maSach);

}

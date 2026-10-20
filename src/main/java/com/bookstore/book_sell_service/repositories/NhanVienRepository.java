package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien,Long> {
    boolean existsByTenDangNhap(String tenDangNhap);
    Optional<NhanVien> findByTenDangNhap(String tenDangNhap);

}

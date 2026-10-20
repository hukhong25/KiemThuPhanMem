package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang,String> {
    public Optional<KhachHang> findByUserName(String userName);
}

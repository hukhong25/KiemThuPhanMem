package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.LoaiSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TheLoaiRepository extends JpaRepository<LoaiSach,Long> {
    Optional<LoaiSach> findByTenLoai(String tenLoai);
}

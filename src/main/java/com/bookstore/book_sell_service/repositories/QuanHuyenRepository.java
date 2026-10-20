package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.QuanHuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface QuanHuyenRepository extends JpaRepository<QuanHuyen,Long> {
    Optional<QuanHuyen> findByTenQuanHuyen(String tenQuanHuyen);
}

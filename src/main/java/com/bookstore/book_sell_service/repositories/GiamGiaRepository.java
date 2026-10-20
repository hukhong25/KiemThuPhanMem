package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.GiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiamGiaRepository extends JpaRepository<GiamGia,Long> {
}

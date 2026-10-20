package com.bookstore.book_sell_service.repositories;

import com.bookstore.book_sell_service.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Long> {
    // Có thể thêm hàm tìm kiếm theo tên nếu cần
    boolean existsByTenTG(String tenTG);
}